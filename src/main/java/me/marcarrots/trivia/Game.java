package me.marcarrots.trivia;

import me.marcarrots.trivia.api.StringSimilarity;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.MessageUtil;
import me.marcarrots.trivia.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collections;

public class Game {
    private final QuestionHolder questionHolder;
    private final Trivia trivia;
    private final BukkitScheduler scheduler;
    private final double similarityScore;
    private final boolean bossBarEnabled;
    private final int timeBetween;
    private long timePerQuestion;
    private int amountOfRounds;
    private boolean doRepetition;
    private CommandSender commandSender;
    private long roundTimeStart;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private RoundResult roundResult;
    private int task;
    private BossBar bossBar;
    private Player roundWinner;
    private String userRightAnswer;

    public Game(Trivia trivia, QuestionHolder questionHolder, long timePerQuestion, int amountOfRounds, boolean doRepetition, CommandSender commandSender) throws IllegalAccessException {
        if (questionHolder.getSize() == 0) {
            throw new IllegalAccessException("There are no Trivia questions loaded. Create some questions before hosting a game!");
        }
        this.trivia = trivia;
        this.timePerQuestion = timePerQuestion;
        this.amountOfRounds = amountOfRounds;
        this.doRepetition = doRepetition;
        this.commandSender = commandSender;

        this.questionHolder = new QuestionHolder(questionHolder);
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.IN_BETWEEN;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.similarityScore = trivia.getConfig().getDouble("Similarity score");
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        this.bossBarEnabled = trivia.getConfig().getBoolean("Enable boss bar", true);
    }

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private void setRandomQuestion() {
        this.currentQuestion = this.questionHolder.getRandomQuestion().getQuestionObj();
    }

    public void start() {
        if (doRepetition) {
            questionHolder.setUniqueQuestions(false);
        } else if (questionHolder.getSize() < amountOfRounds) {
            commandSender.sendMessage("There are more rounds than questions, so questions will repeat.");
            questionHolder.setUniqueQuestions(false);
        } else {
            questionHolder.setUniqueQuestions(true);
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
        startBossBar();
        timer = new Timer(trivia, amountOfRounds, timePerQuestion, bossBar,
                () -> { // after game
                    handleRoundOutcome();
                    Effects.playSoundToAll("Game over sound", trivia.getConfig(), "Game over pitch");
                    scores.broadcastLargestScores();
                    scores = null;
                    trivia.clearGame();
                    gameOverBossBar();
                },
                (t) -> { // after each round
                    roundTimeStart = System.currentTimeMillis();
                    handleRoundOutcome();
                    handleNextQuestion(t);
                }
        );
        timer.handleNextRound();
    }


    private void handleRoundOutcome() {
        if (roundResult == RoundResult.UNANSWERED) { // if time ran out
            Lang.broadcastMessage(Lang.TIME_UP.format_multiple(new Placeholder.PlaceholderBuilder()
                    .question(currentQuestion.getQuestionString())
                    .answer(currentQuestion.getAnswerList())
                    .questionNum(getQuestionNum())
                    .totalQuestionNum(amountOfRounds)
                    .build()
            ));
            Effects.playSoundToAll("Time up sound", trivia.getConfig(), "Time up pitch");
        } else if (roundResult == RoundResult.SKIPPED) { // if round was skipped
            afterAnswerFillBossBar(BarColor.YELLOW);
            Lang.broadcastMessage(Lang.SKIP.format_multiple(new Placeholder.PlaceholderBuilder()
                    .question(currentQuestion.getQuestionString())
                    .answer(currentQuestion.getAnswerList())
                    .questionNum(getQuestionNum())
                    .build()
            ));
        } else if (roundResult == RoundResult.ANSWERED) { // if question was answered
            String timeToAnswer = Elapsed.millisToElapsedTime(roundTimeStart).getElapsedFormattedString();
            afterAnswerFillBossBar(BarColor.GREEN);
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
                scores.addScore(roundWinner);
                trivia.getRewards()[0].giveReward(roundWinner);
                roundWinner = null;
            }
            userRightAnswer = null;
        }
        roundResult = RoundResult.IN_BETWEEN;
    }


    private void handleNextQuestion(Timer t) {
        currentQuestion = null;
        task = scheduler.scheduleSyncDelayedTask(trivia, () -> {
            roundResult = RoundResult.UNANSWERED;
            setRandomQuestion();
            t.startTimer();
            perRoundBossBarUpdate();
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
        hideBossBar();
        scheduler.cancelTask(task);
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

    private void startBossBar() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar = Bukkit.createBossBar(Lang.TRIVIA_START.format_single(), BarColor.YELLOW, BarStyle.SOLID);
        bossBar.setProgress(0);
        if (amountOfRounds % 10 == 0) {
            if (amountOfRounds % 20 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_20);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_10);
            }
        } else if (amountOfRounds % 6 == 0) {
            if (amountOfRounds % 12 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_12);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_6);
            }
        }
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> bossBar.addPlayer(p));
    }

    private void perRoundBossBarUpdate() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setTitle(Lang.BOSS_BAR_INFO.format_single(new Placeholder.PlaceholderBuilder()
                .questionNum(getQuestionNum())
                .totalQuestionNum(amountOfRounds)
                .build()
        ));
        bossBar.setColor(BarColor.RED);
        bossBar.setProgress(((float) getQuestionNum() - 1) / amountOfRounds);
    }

    private void afterAnswerFillBossBar(BarColor color) {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setColor(color);
        double incrementAmt = 1 / ((double) amountOfRounds * 20);
        double goal = ((float) getQuestionNum() - 1) / amountOfRounds;
        new BukkitRunnable() {
            @Override
            public void run() {
                double currentProgress = bossBar.getProgress();
                double amtToSet = currentProgress + incrementAmt;
                if (currentProgress < goal) {
                    if (amtToSet >= 1) {
                        bossBar.setProgress(1);
                        this.cancel();
                    } else if (amtToSet >= goal) {
                        bossBar.setProgress(goal);
                    } else {
                        currentProgress = amtToSet;
                        bossBar.setProgress(currentProgress);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(trivia, 0, 1);
    }

    public void hideBossBar() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setVisible(true);
        bossBar.removeAll();
    }

    public void gameOverBossBar() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setTitle(Lang.BOSS_BAR_GAME_OVER.format_single());
        bossBar.setColor(BarColor.GREEN);
        new BukkitRunnable() {
            boolean turn = false;

            @Override
            public void run() {
                if (turn) {
                    hideBossBar();
                    this.cancel();
                }
                bossBar.setTitle(Lang.BOSS_BAR_THANKS.format_single());
                turn = true;
            }
        }.runTaskTimer(trivia, 100, 100);
    }

    public void showBarToPlayer(Player player) {
        bossBar.addPlayer(player);
    }

}
