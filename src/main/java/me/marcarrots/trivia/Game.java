package me.marcarrots.trivia;

import me.marcarrots.trivia.api.StringSimilarity;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.MessageUtil;
import me.marcarrots.trivia.language.Placeholder;
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
    private final Trivia trivia;
    private final BukkitScheduler scheduler;
    private final double similarityScore;
    private final int timeBetween;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;
    private final CommandSender commandSender;
    private final GameBossBar gameBossBar;
    private long roundTimeStart;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private RoundResult roundResult;
    private int task;
    private Player roundWinner;
    private String userRightAnswer;

    public Game(Trivia trivia, long timePerQuestion, int amountOfRounds, boolean doRepetition, CommandSender commandSender) throws IllegalAccessException {
        if (trivia.getQuestionHolder().getSize() == 0) {
            throw new IllegalAccessException("There are no Trivia questions loaded. Create some questions before hosting a game!");
        }
        this.trivia = trivia;
        this.timePerQuestion = timePerQuestion;
        this.amountOfRounds = amountOfRounds;
        this.doRepetition = doRepetition;
        this.commandSender = commandSender;
        this.questionContainer = new QuestionContainer(trivia.getQuestionHolder());
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.IN_BETWEEN;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.similarityScore = trivia.getConfig().getDouble("Similarity score", 1);
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        boolean bossBarEnabled = trivia.getConfig().getBoolean("Enable boss bar", true);
        gameBossBar = new GameBossBar(trivia, bossBarEnabled);
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
            commandSender.sendMessage(ChatColor.RED + "There are more rounds than questions, so questions will repeat.");
            questionContainer.setUniqueQuestions(false);
        } else {
            questionContainer.setUniqueQuestions(true);
        }

        String border = Lang.BORDER.format_single();

        commandSender.sendMessage(border);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lTrivia match started!"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eGame summary:"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&0- &eRounds: &f") + amountOfRounds);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&0- &eSeconds per round: &f") + timePerQuestion);
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&0- &eRepeat questions: &f") + doRepetition);
        commandSender.sendMessage(border);

        scores.addOnlinePlayersToGame();
        Lang.broadcastMessage(Lang.TRIVIA_START.format_multiple(null));
        Effects.playSoundToAll("Game start sound", trivia.getConfig(), "Game start pitch");
        gameBossBar.startBossBar(amountOfRounds);
        timer = new Timer(trivia, amountOfRounds, timePerQuestion, gameBossBar,
                (t) -> { // after each round
                    roundTimeStart = System.currentTimeMillis();
                    handleRoundOutcome();
                    handleNextQuestion(t);
                },
                () -> { // after game
                    handleRoundOutcome();
                    Effects.playSoundToAll("Game over sound", trivia.getConfig(), "Game over pitch");
                    scores.deliverRewardsToWinners();
                    scores = null;
                    trivia.clearGame();
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
                Lang.broadcastMessage(Lang.SOLVED_MESSAGE.format_multiple(new Placeholder.PlaceholderBuilder()
                        .player(roundWinner)
                        .question(currentQuestion.getQuestionString())
                        .answer(Collections.singletonList(userRightAnswer))
                        .questionNum(getQuestionNum())
                        .totalQuestionNum(amountOfRounds)
                        .elapsedTime(timeToAnswer)
                        .build()
                ));
                if (roundWinner != null) {
                    Effects.playSound(roundWinner, trivia.getConfig(), "Answer correct sound", "Answer correct pitch");
                    scores.addScore(roundWinner, getQuestionNum());
                    trivia.getRewards()[0].giveReward(roundWinner);
                    roundWinner = null;
                }
                userRightAnswer = null;
                break;

            case SKIPPED:
                gameBossBar.fillAfterAnswer(BarColor.YELLOW, getQuestionNum(), amountOfRounds);
                Lang.broadcastMessage(Lang.SKIP.format_multiple(new Placeholder.PlaceholderBuilder()
                        .question(currentQuestion.getQuestionString())
                        .answer(currentQuestion.getAnswerList())
                        .questionNum(getQuestionNum())
                        .build()
                ));
                break;

            case UNANSWERED:
                Lang.broadcastMessage(Lang.TIME_UP.format_multiple(new Placeholder.PlaceholderBuilder()
                        .question(currentQuestion.getQuestionString())
                        .answer(currentQuestion.getAnswerList())
                        .questionNum(getQuestionNum())
                        .totalQuestionNum(amountOfRounds)
                        .build()
                ));
                Effects.playSoundToAll("Time up sound", trivia.getConfig(), "Time up pitch");
                break;

            case HALTED:
                return;
        }

        roundResult = RoundResult.IN_BETWEEN;
    }


    private void handleNextQuestion(Timer t) {
        currentQuestion = null;
        task = scheduler.scheduleSyncDelayedTask(trivia, () -> {
            roundResult = RoundResult.UNANSWERED;
            setRandomQuestion();
            t.startTimer();
            gameBossBar.perRoundBossBarUpdate(getQuestionNum(), amountOfRounds);
            Lang.broadcastMessage(Lang.QUESTION.format_multiple(new Placeholder.PlaceholderBuilder()
                    .question(currentQuestion.getQuestionString())
                    .answer(currentQuestion.getAnswerList())
                    .questionNum(getQuestionNum())
                    .totalQuestionNum(amountOfRounds)
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
            if (StringSimilarity.similarity(userAnswerStripped.toLowerCase(), correctAnswerStripped.toLowerCase()) >= similarityScore) {
                roundResult = RoundResult.ANSWERED;

                trivia.getStats().addRoundWon(player);

                Bukkit.getScheduler().scheduleSyncDelayedTask(trivia, () -> {
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
