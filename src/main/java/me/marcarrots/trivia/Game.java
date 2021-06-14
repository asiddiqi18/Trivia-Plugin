package me.marcarrots.trivia;

import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.LangBuilder;
import me.marcarrots.trivia.api.StringSimilarity;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
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

    public Game(Trivia trivia, QuestionHolder questionHolder) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.INITIAL;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.similarityScore = trivia.getConfig().getDouble("Similarity score");
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        this.bossBarEnabled = trivia.getConfig().getBoolean("Enable boss bar", true);
    }

    public void setParameters(PlayerMenuUtility playerMenuUtility) {
        timePerQuestion = playerMenuUtility.getTimePer();
        amountOfRounds = playerMenuUtility.getTotalRounds();
        doRepetition = playerMenuUtility.isRepeatEnabled();
        commandSender = playerMenuUtility.getOwner();
    }

    public void setParameters(CommandSender sender, int rounds) {
        timePerQuestion = trivia.getConfig().getLong("Default time per round", 10L);
        amountOfRounds = rounds;
        doRepetition = false;
        commandSender = sender;
    }

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private void setRandomQuestion() {
        this.currentQuestion = this.questionHolder.getRandomQuestion().getQuestionObj();
    }

    public void start() {
        if (questionHolder.getSize() == 0) {
            commandSender.sendMessage(ChatColor.RED + "There are no trivia questions loaded.");
            return;
        }
        if (doRepetition) {
            questionHolder.setUniqueQuestions(false);
        } else if (questionHolder.getSize() < amountOfRounds) {
            commandSender.sendMessage("There are more rounds than questions, so questions will repeat.");
            questionHolder.setUniqueQuestions(false);
        } else {
            questionHolder.setUniqueQuestions(true);
        }

        String border = Lang.BORDER.format_single(null);

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
        timer.startTimerInitial();
    }


    private void handleRoundOutcome() {
        if (roundResult == RoundResult.UNANSWERED) { // if time ran out
            Lang.broadcastMessage(Lang.TIME_UP.format_multiple(new LangBuilder()
                    .setQuestion(currentQuestion.getQuestionString())
                    .setAnswer(String.valueOf(currentQuestion.getAnswerList()))
                    .setQuestionNum(getQuestionNum())
                    .setTotalQuestionNum(amountOfRounds)
            ));
            Effects.playSoundToAll("Time up sound", trivia.getConfig(), "Time up pitch");
        } else if (roundResult == RoundResult.SKIPPED) { // if round was skipped
            afterAnswerFillBossBar(BarColor.YELLOW);
            Lang.broadcastMessage(Lang.SKIP.format_multiple(new LangBuilder()
                    .setQuestion(currentQuestion.getQuestionString())
                    .setAnswer(String.valueOf(currentQuestion.getAnswerList()))
                    .setQuestionNum(getQuestionNum())
            ));
        } else if (roundResult == RoundResult.ANSWERED) { // if question was answered
            String timeToAnswer = Timer.getElapsedTime(roundTimeStart);
            afterAnswerFillBossBar(BarColor.GREEN);
            Lang.broadcastMessage(Lang.SOLVED_MESSAGE.format_multiple(new LangBuilder()
                    .setPlayer(roundWinner)
                    .setQuestion(currentQuestion.getQuestionString())
                    .setAnswer(userRightAnswer)
                    .setQuestionNum(getQuestionNum())
                    .setTotalQuestionNum(amountOfRounds)
                    .setElapsedTime(timeToAnswer)
            ));
            if (roundWinner != null) {
                Effects.playSound(roundWinner, trivia.getConfig(), "Answer correct sound", "Answer correct pitch");
                scores.addScore(roundWinner);
                trivia.getRewards()[0].giveReward(roundWinner);
                roundWinner = null;
            }
            userRightAnswer = null;
            roundResult = RoundResult.INITIAL;
        }
    }

    private void handleNextQuestion(Timer t) {
        currentQuestion = null;
        task = scheduler.scheduleSyncDelayedTask(trivia, () -> {
            roundResult = RoundResult.UNANSWERED;
            setRandomQuestion();
            t.startTimer();
            perRoundBossBarUpdate();
            Lang.broadcastMessage(Lang.QUESTION.format_multiple(new LangBuilder()
                    .setQuestion(currentQuestion.getQuestionString())
                    .setAnswer(String.valueOf(currentQuestion.getAnswerList()))
                    .setQuestionNum(getQuestionNum())
                    .setTotalQuestionNum(amountOfRounds)
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

        if (currentQuestion == null) {
            return;
        }

        String userAnswer = e.getMessage();

        for (String correctAnswer : currentQuestion.getAnswerList()) {
            if (StringSimilarity.similarity(userAnswer.toLowerCase(), correctAnswer.toLowerCase()) >= similarityScore) {
                handleRightAnswer(e.getPlayer(), correctAnswer);
                break;
            }
            else if (userAnswer.toLowerCase().endsWith(correctAnswer.toLowerCase())) {
                handleRightAnswer(e.getPlayer(), correctAnswer);
                break;
            }
        }
    }

    private void handleRightAnswer(Player player, String rightAnswer) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(trivia, () -> {
                roundWinner = player;
                userRightAnswer = rightAnswer;
                roundResult = RoundResult.ANSWERED;
                timer.nextQuestion();
        }, 2L);
    }

    public boolean forceSkipRound() {
        if (currentQuestion == null) {
            return false;
        }
        roundResult = RoundResult.SKIPPED;
        timer.nextQuestion();
        return true;
    }

    private void startBossBar() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar = Bukkit.createBossBar(Lang.TRIVIA_START.format_single(null), BarColor.YELLOW, BarStyle.SOLID);
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
        bossBar.setTitle(Lang.BOSS_BAR_INFO.format_single(new LangBuilder()
                .setQuestionNum(getQuestionNum())
                .setTotalQuestionNum(amountOfRounds)
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
        bossBar.setTitle(Lang.BOSS_BAR_GAME_OVER.format_single(null));
        bossBar.setColor(BarColor.GREEN);
        new BukkitRunnable() {
            boolean turn = false;

            @Override
            public void run() {
                if (turn) {
                    hideBossBar();
                    this.cancel();
                }
                bossBar.setTitle(Lang.BOSS_BAR_THANKS.format_single(null));
                turn = true;
            }
        }.runTaskTimer(trivia, 100, 100);
    }

    public void showBarToPlayer(Player player) {
        bossBar.addPlayer(player);
    }

}
