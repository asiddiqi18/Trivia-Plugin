package me.marcarrots.trivia;

import me.marcarrots.trivia.api.StringSimilarity;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

public class Game {
    private final QuestionHolder questionHolder;
    private final Trivia trivia;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;
    private final double similarityScore;
    private final int timeBetween;
    private final CommandSender player;
    private final BukkitScheduler scheduler;
    private long roundTimeStart;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private RoundResult roundResult;
    private String correctAnswer;
    private int task;

    public Game(Trivia trivia, QuestionHolder questionHolder, PlayerMenuUtility playerMenuUtility) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.timePerQuestion = playerMenuUtility.getTimePer();
        this.amountOfRounds = playerMenuUtility.getTotalRounds();
        this.doRepetition = playerMenuUtility.isRepeatEnabled();
        this.player = playerMenuUtility.getOwner();
        this.similarityScore = trivia.getConfig().getDouble("Similarity score");
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.ANSWERED;
        this.scheduler = Bukkit.getServer().getScheduler();
    }

    public Game(Trivia trivia, QuestionHolder questionHolder, CommandSender sender) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.player = sender;
        this.amountOfRounds = trivia.getConfig().getInt("Default rounds", 10);
        this.timePerQuestion = trivia.getConfig().getLong("Default time per round", 10L);
        this.doRepetition = false;
        this.similarityScore = trivia.getConfig().getDouble("Similarity score");
        this.timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        this.scores = new PlayerScoreHolder(trivia);
        this.roundResult = RoundResult.ANSWERED;
        this.scheduler = Bukkit.getServer().getScheduler();
        this.roundTimeStart = 0;
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
        playSoundToAll("Game start sound", "Game start pitch");
        timer = new Timer(trivia, amountOfRounds, timePerQuestion,
                () -> { // after game
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER.format(null));
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER_WINNER_LINE.format(null));
                    playSoundToAll("Game over sound", "Game over pitch");
                    scores.broadcastLargestScores();
                    scores = null;
                    trivia.clearGame();
                },
                (t) -> { // after each round
                    roundTimeStart = System.currentTimeMillis();
                    if (roundResult.equals(RoundResult.UNANSWERED)) {
                        Bukkit.broadcastMessage(Lang.TIME_UP.format(new LangBuilder()
                                .setQuestion(getCurrentQuestion().getQuestionString())
                                .setAnswer(String.valueOf(getCurrentQuestion().getAnswerList()))
                                .setQuestionNum(getQuestionNum())
                        ));

                        playSoundToAll("Time up sound", "Time up pitch");
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
                    Bukkit.broadcastMessage(Lang.SOLVED_MESSAGE.format(new LangBuilder()
                            .setPlayer(e.getPlayer())
                            .setQuestion(getCurrentQuestion().getQuestionString())
                            .setAnswer(correctAnswer)
                            .setQuestionNum(getQuestionNum())
                            .setElapsedTime(timeToAnswer)
                    ));
                    playSound(e.getPlayer(), "Answer correct sound", "Answer correct pitch");
                    scores.addScore(e.getPlayer());
                    roundResult = RoundResult.ANSWERED;
                    timer.nextQuestion();
                }, 2L);
            }
        }
    }

    private void playSound(Player player, String soundPath, String pitchPath) {
        String soundString = trivia.getConfig().getString(soundPath);
        try {
            float pitchVal = Float.parseFloat(Objects.requireNonNull(trivia.getConfig().getString(pitchPath, "1")));
            player.playSound(player.getLocation(), Sound.valueOf(soundString), 0.6F, pitchVal);
        } catch (IllegalArgumentException | NullPointerException exception) {
            if (soundString != null && !soundString.equalsIgnoreCase("none")) {
                switch (soundPath) {
                    case "Answer correct sound":
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.5F);
                        break;
                    case "Time up sound":
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.6F, 0.9F);
                        break;
                }
            }
        }
    }

    private void playSoundToAll(String path, String pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playSound(player, path, pitch);
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
}
