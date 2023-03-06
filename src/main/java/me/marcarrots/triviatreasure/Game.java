package me.marcarrots.triviatreasure;

import me.marcarrots.triviatreasure.effects.GameBossBar;
import me.marcarrots.triviatreasure.effects.GameSound;
import me.marcarrots.triviatreasure.effects.SoundType;
import me.marcarrots.triviatreasure.language.Lang;
import me.marcarrots.triviatreasure.language.MessageUtil;
import me.marcarrots.triviatreasure.language.Placeholder;
import me.marcarrots.triviatreasure.utils.Broadcaster;
import me.marcarrots.triviatreasure.utils.Elapsed;
import me.marcarrots.triviatreasure.utils.StringSimilarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;

public class Game {
    private final QuestionContainer questionContainer;
    private final TriviaTreasure triviaTreasure;
    private final BukkitScheduler scheduler;
    private final double similarityScore;
    private final int timeBetween;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;
    private final CommandSender commandSender;
    private final GameBossBar gameBossBar;
    private final GameSound gameSound;
    private final Placeholder.PlaceholderBuilder placeholderBuilder;
    private long roundTimeStart;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private RoundResult roundResult;
    private int task;
    private Player roundWinner;
    private String userRightAnswer;


    public Game(TriviaTreasure triviaTreasure, CommandSender commandSender, long timePerQuestion, int amountOfRounds, boolean doRepetition) throws IllegalAccessException {
        if (triviaTreasure.getQuestionHolder().getSize() == 0) {
            throw new IllegalAccessException(Lang.NO_QUESTIONS_LOADED.format_single());
        }
        this.triviaTreasure = triviaTreasure;
        this.timePerQuestion = timePerQuestion;
        this.amountOfRounds = amountOfRounds;
        this.doRepetition = doRepetition;
        this.commandSender = commandSender;
        this.questionContainer = new QuestionContainer(triviaTreasure.getQuestionHolder());
        this.scores = new PlayerScoreHolder(triviaTreasure);
        this.roundResult = RoundResult.IN_BETWEEN;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.similarityScore = triviaTreasure.getConfig().getDouble("Similarity score", 1);
        this.timeBetween = triviaTreasure.getConfig().getInt("Time between rounds", 2);
        boolean bossBarEnabled = triviaTreasure.getConfig().getBoolean("Enable boss bar", true);
        gameBossBar = new GameBossBar(triviaTreasure, bossBarEnabled);
        gameSound = new GameSound(triviaTreasure);

        placeholderBuilder = new Placeholder.PlaceholderBuilder().totalQuestionNum(amountOfRounds);

    }

    public GameBossBar getGameBossBar() {
        return gameBossBar;
    }

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private void setRandomQuestion() {
        this.currentQuestion = this.questionContainer.getRandomQuestion();
    }

    public void start() {
        if (doRepetition) {
            questionContainer.setUniqueQuestions(false);
        } else if (questionContainer.getSize() < amountOfRounds) {
            commandSender.sendMessage(Lang.FORCE_REPEAT.format_single());
            questionContainer.setUniqueQuestions(false);
        } else {
            questionContainer.setUniqueQuestions(true);
        }

        String border = Lang.BORDER.format_single();

        commandSender.sendMessage(border);
        commandSender.sendMessage(Lang.GAME_SUMMARY_MATCH_STARTED.format_single());
        commandSender.sendMessage(Lang.GAME_SUMMARY_SUMMARY.format_single());
        commandSender.sendMessage(Lang.GAME_SUMMARY_ROUNDS.format_multiple(placeholderBuilder.val(String.valueOf(amountOfRounds)).build()));
        commandSender.sendMessage(Lang.GAME_SUMMARY_SECONDS_PER.format_multiple(placeholderBuilder.val(String.valueOf(timePerQuestion)).build()));
        commandSender.sendMessage(Lang.GAME_SUMMARY_REPEAT_ENABLED.format_multiple(placeholderBuilder.val(String.valueOf(doRepetition)).build()));
        commandSender.sendMessage(border);

        scores.addOnlinePlayersToGame();
        Broadcaster.broadcastMessage(Lang.TRIVIA_START.format_multiple(null));
        gameSound.playSoundToAll(SoundType.GAME_START);
        gameBossBar.startBossBar(amountOfRounds);
        timer = new Timer(triviaTreasure, amountOfRounds, timePerQuestion, gameBossBar,
                (t) -> { // after each round
                    roundTimeStart = System.currentTimeMillis();
                    handleRoundOutcome();
                    handleNextQuestion(t);
                },
                () -> { // after game
                    handleRoundOutcome();
                    gameSound.playSoundToAll(SoundType.GAME_OVER);
                    scores.deliverRewardsToWinners();
                    scores = null;
                    triviaTreasure.clearGame();
                    if (roundResult == RoundResult.HALTED) {
                        gameBossBar.gameOverBossBar(BarColor.RED, Lang.BOSS_BAR_HALTED.format_single());
                    } else {
                        gameBossBar.gameOverBossBar(BarColor.GREEN, Lang.BOSS_BAR_GAME_OVER.format_single());
                    }
                }
        );
        timer.handleNextRound();
    }

    private void handleRoundOutcome() {
        switch (roundResult) {
            case ANSWERED:
                String timeToAnswer = Elapsed.millisToElapsedTime(roundTimeStart).getElapsedFormattedString();
                gameBossBar.fillAfterAnswer(BarColor.GREEN, getQuestionNum(), amountOfRounds);
                Broadcaster.broadcastMessage(Lang.SOLVED_MESSAGE.format_multiple(placeholderBuilder
                        .player(roundWinner)
                        .question(currentQuestion.getQuestionString())
                        .answer(Collections.singletonList(userRightAnswer))
                        .questionNum(getQuestionNum())
                        .elapsedTime(timeToAnswer)
                        .build()
                ));
                if (roundWinner != null) {
                    gameSound.playSound(roundWinner, SoundType.CORRECT);
                    scores.addScore(roundWinner, getQuestionNum());
                    triviaTreasure.getRewards()[0].giveReward(roundWinner);
                    roundWinner = null;
                }
                userRightAnswer = null;
                break;

            case SKIPPED:
                gameBossBar.fillAfterAnswer(BarColor.YELLOW, getQuestionNum(), amountOfRounds);
                gameSound.playSoundToAll(SoundType.QUESTION_SKIPPED);
                Broadcaster.broadcastMessage(Lang.SKIP.format_multiple(placeholderBuilder
                        .question(currentQuestion.getQuestionString())
                        .answer(currentQuestion.getAnswerList())
                        .questionNum(getQuestionNum())
                        .build()
                ));
                break;

            case UNANSWERED:
                Broadcaster.broadcastMessage(Lang.TIME_UP.format_multiple(placeholderBuilder
                        .question(currentQuestion.getQuestionString())
                        .answer(currentQuestion.getAnswerList())
                        .questionNum(getQuestionNum())
                        .build()
                ));
                gameSound.playSoundToAll(SoundType.TIME_UP);
                break;

            case HALTED:
                return;
        }

        roundResult = RoundResult.IN_BETWEEN;
    }

    private void handleNextQuestion(Timer t) {
        currentQuestion = null;
        task = scheduler.scheduleSyncDelayedTask(triviaTreasure, () -> {
            roundResult = RoundResult.UNANSWERED;
            setRandomQuestion();
            t.startTimer();
            gameBossBar.perRoundBossBarUpdate(getQuestionNum(), amountOfRounds);
            Broadcaster.broadcastMessage(Lang.QUESTION.format_multiple(placeholderBuilder
                    .question(currentQuestion.getQuestionString())
                    .answer(currentQuestion.getAnswerList())
                    .questionNum(getQuestionNum())
                    .build()
            ));
        }, timeBetween * 20L);
    }

    public void stop() {
        gameBossBar.fillAfterStop();
        scheduler.cancelTask(task);
        roundResult = RoundResult.HALTED;
        timer.endTimer();
    }

    private int getQuestionNum() {
        return Math.subtractExact(timer.getRounds(), timer.getRoundsLeft());
    }

    public void playerAnswer(AsyncPlayerChatEvent e) {

        if (currentQuestion == null || roundResult != RoundResult.UNANSWERED) {
            return;
        }

        String userAnswerStripped = ChatColor.stripColor(e.getMessage());
        Player player = e.getPlayer();

        for (String correctAnswer : currentQuestion.getAnswerList()) {
            String correctAnswerStripped = ChatColor.stripColor(MessageUtil.HexColorMessage(correctAnswer));
            if (StringSimilarity.calculateSimilarity(userAnswerStripped.toLowerCase(), correctAnswerStripped.toLowerCase()) >= similarityScore) {
                roundResult = RoundResult.ANSWERED;

                triviaTreasure.getStats().addRoundWon(player);

                Bukkit.getScheduler().scheduleSyncDelayedTask(triviaTreasure, () -> {
                    roundWinner = player;
                    userRightAnswer = correctAnswer;
                    timer.handleNextRound();
                }, 2L);
                return;
            }
        }
    }

    public boolean forceSkipRound() {
        if (currentQuestion == null) {
            return false;
        }
        roundResult = RoundResult.SKIPPED;
        timer.handleNextRound();
        return true;
    }

}
