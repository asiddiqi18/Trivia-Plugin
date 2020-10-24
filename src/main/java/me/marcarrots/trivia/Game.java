package me.marcarrots.trivia;

import me.marcarrots.trivia.api.StringSimilarity;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;

public class Game {
    private final QuestionHolder questionHolder;
    private final Trivia trivia;
    private long timePerQuestion;
    private int amountOfRounds;
    private boolean doRepetition;
    private double similarityScore;
    private int timeBetween;
    private CommandSender player;
    private final BukkitScheduler scheduler;
    private long roundTimeStart;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private RoundResult roundResult;
    private String correctAnswer;
    private int task;
    private BossBar bossBar;

    public Game(Trivia trivia, QuestionHolder questionHolder) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.ANSWERED;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.similarityScore = trivia.getConfig().getDouble("Similarity score");
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
    }

    public void setParameters(PlayerMenuUtility playerMenuUtility) {
        timePerQuestion = playerMenuUtility.getTimePer();
        amountOfRounds = playerMenuUtility.getTotalRounds();
        doRepetition = playerMenuUtility.isRepeatEnabled();
        player = playerMenuUtility.getOwner();
    }

    public void setParameters(CommandSender sender) {
        timePerQuestion = trivia.getConfig().getLong("Default time per round", 10L);
        amountOfRounds = trivia.getConfig().getInt("Default rounds", 10);
        doRepetition = false;
        player = sender;
    }

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private void setRandomQuestion() {
        this.currentQuestion = this.questionHolder.getRandomQuestion().getQuestionObj();
    }

    private Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void start() {
        if (questionHolder.getSize() == 0) {
            player.sendMessage(ChatColor.RED + "There are no trivia questions loaded.");
            return;
        }
        if (doRepetition) {
            questionHolder.setUniqueQuestions(false);
        } else if (questionHolder.getSize() < amountOfRounds) {
            player.sendMessage("There are more rounds than questions, so questions will repeat.");
            questionHolder.setUniqueQuestions(false);
        } else {
            questionHolder.setUniqueQuestions(true);
        }
        scores.addPlayersToGame();
        Bukkit.broadcastMessage(Lang.TRIVIA_START.format(null));
        Effects.playSoundToAll("Game start sound", trivia.getConfig(), "Game start pitch");
        startBossBar();
        timer = new Timer(trivia, amountOfRounds, timePerQuestion,
                () -> { // after game
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER.format(null));
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER_WINNER_LINE.format(null));
                    Effects.playSoundToAll("Game over sound", trivia.getConfig(), "Game over pitch");
                    scores.broadcastLargestScores();
                    scores = null;
                    trivia.clearGame();
                    hideBossBar();
                },
                (t) -> { // after each round
                    roundTimeStart = System.currentTimeMillis();
                    if (roundResult.equals(RoundResult.UNANSWERED)) {
                        Bukkit.broadcastMessage(Lang.TIME_UP.format(new LangBuilder()
                                .setQuestion(getCurrentQuestion().getQuestionString())
                                .setAnswer(String.valueOf(getCurrentQuestion().getAnswerList()))
                                .setQuestionNum(getQuestionNum())
                        ));

                        Effects.playSoundToAll("Time up sound", trivia.getConfig(), "Time up pitch");
                    } else if (roundResult.equals(RoundResult.SKIPPED)) {
                        Bukkit.broadcastMessage(Lang.SKIP.format(new LangBuilder()
                                .setQuestion(getCurrentQuestion().getQuestionString())
                                .setAnswer(String.valueOf(getCurrentQuestion().getAnswerList()))
                                .setQuestionNum(getQuestionNum())
                        ));
                    }
                    currentQuestion = null;
                    scheduler.cancelTask(t.getAssignedTaskId());
                    task = scheduler.scheduleSyncDelayedTask(trivia, () -> {
                        roundResult = RoundResult.UNANSWERED;
                        setRandomQuestion();
                        t.scheduleTimer();

                        bossBar.setTitle(String.format("Trivia Match (%d/%d)", getQuestionNum(), amountOfRounds));
                        bossBar.setColor(BarColor.RED);
                        bossBar.setProgress((float)getQuestionNum()/amountOfRounds);
                        Bukkit.broadcastMessage(Lang.QUESTION.format(new LangBuilder()
                                .setQuestion(getCurrentQuestion().getQuestionString())
                                .setAnswer(correctAnswer)
                                .setQuestionNum(getQuestionNum())
                        ));
                    }, timeBetween * 20);
                }
        );
        timer.scheduleTimerInitialize();
    }

    public void stop() {
        hideBossBar();
        scheduler.cancelTask(task);
        timer.stop();
    }

    private int getQuestionNum() {
        return Math.subtractExact(timer.getRounds(), timer.getRoundsLeft());
    }

    public void playerAnswer(AsyncPlayerChatEvent e) {
        if (currentQuestion == null)
            return;
        for (String answer : currentQuestion.getAnswerList()) {
            if (StringSimilarity.similarity(e.getMessage(), answer) >= similarityScore) {
                correctAnswer = answer;
                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(trivia, () -> {
                    String timeToAnswer = Trivia.getElapsedTime(roundTimeStart);
                    bossBar.setColor(BarColor.GREEN);
                    Bukkit.broadcastMessage(Lang.SOLVED_MESSAGE.format(new LangBuilder()
                            .setPlayer(e.getPlayer())
                            .setQuestion(getCurrentQuestion().getQuestionString())
                            .setAnswer(correctAnswer)
                            .setQuestionNum(getQuestionNum())
                            .setElapsedTime(timeToAnswer)
                    ));
                    Effects.playSound(e.getPlayer(), trivia.getConfig(), "Answer correct sound", "Answer correct pitch");
                    scores.addScore(e.getPlayer());
                    roundResult = RoundResult.ANSWERED;
                    timer.nextQuestion();
                }, 2L);
            }
        }
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
        bossBar = Bukkit.createBossBar(Lang.TRIVIA_START.format(null), BarColor.YELLOW, BarStyle.SOLID);
        bossBar.setProgress(0);
        if (amountOfRounds % 10 == 0) {
            if (amountOfRounds % 20 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_20);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_10);
            }
        } else if (amountOfRounds % 6 == 0) {
            if (amountOfRounds %  12 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_12);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_6);
            }
        }
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> bossBar.addPlayer(p));
    }

    public void hideBossBar() {
        bossBar.setVisible(true);
        bossBar.removeAll();
    }

}
