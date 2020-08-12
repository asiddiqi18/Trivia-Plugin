package me.marcarrots.trivia;

import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.utils.StringSimilarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class Game {

    private final QuestionHolder questionHolder;
    private final Trivia trivia;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;
    private final double similarityScore;
    private final int timeBetween;
    private final CommandSender player;
    BukkitScheduler scheduler;
    private PlayerScoreHolder scores;
    private Question currentQuestion;
    private Timer timer;
    private boolean wasAnswered;
    private int task;

    // when initiated through menu
    public Game(Trivia trivia, QuestionHolder questionHolder, PlayerMenuUtility playerMenuUtility) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        timePerQuestion = playerMenuUtility.getTimePer();
        amountOfRounds = playerMenuUtility.getTotalRounds();
        doRepetition = playerMenuUtility.isRepeatEnabled();
        player = playerMenuUtility.getOwner();
        similarityScore = trivia.getConfig().getDouble("Similarity score");
        timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        scores = new PlayerScoreHolder(trivia);
        wasAnswered = true;
        scheduler = getServer().getScheduler();
    }

    // when initiated through command
    public Game(Trivia trivia, QuestionHolder questionHolder, CommandSender sender) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.player = sender;
        amountOfRounds = trivia.getConfig().getInt("Default rounds", 10);
        timePerQuestion = trivia.getConfig().getLong("Default time per round", 10L);
        doRepetition = false;
        similarityScore = trivia.getConfig().getDouble("Similarity score");
        timeBetween = trivia.getConfig().getInt("Time between rounds", 2);
        scores = new PlayerScoreHolder(trivia);
        wasAnswered = true;
        scheduler = getServer().getScheduler();
    }

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private void setRandomQuestion() {
        currentQuestion = questionHolder.getRandomQuestion().getQuestionObj();
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
        } else {
            if (questionHolder.getSize() < amountOfRounds) {
                player.sendMessage("There are more rounds than questions, so questions will repeat.");
                questionHolder.setUniqueQuestions(false);
            } else {
                questionHolder.setUniqueQuestions(true);
            }
        }

        scores.addPlayersToGame();
        Bukkit.broadcastMessage(Lang.TRIVIA_START.format());
        playSoundToAll("Game start sound", "Game start pitch");
        timer = new Timer(trivia, amountOfRounds, timePerQuestion,
                () -> { // after game
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER.format());
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER_WINNER_LINE.format());
                    playSoundToAll("Game over sound", "Game over pitch");
                    scores.broadcastLargestScores();
                    scores = null;
                    trivia.clearGame();
                },
                (t) -> { // after each round
                    if (!wasAnswered) {
                        Bukkit.broadcastMessage(Lang.TIME_UP.format());
                        playSoundToAll("Time up sound", "Time up pitch");
                    }

                    currentQuestion = null;

                    scheduler.cancelTask(t.getAssignedTaskId());

                    task = scheduler.scheduleSyncDelayedTask(trivia, () -> {
                        wasAnswered = false;
                        setRandomQuestion();
                        t.scheduleTimer();
                        Bukkit.broadcastMessage(Lang.QUESTION.format(null, getCurrentQuestion().getQuestionString(), getCurrentQuestion().getAnswerString(), getQuestionNum(), 0));
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

        if (currentQuestion == null) {
            return;
        }

        if (StringSimilarity.similarity(e.getMessage(), currentQuestion.getAnswerString()) >= similarityScore) {
            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(trivia, () -> {
                Bukkit.broadcastMessage(Lang.SOLVED_MESSAGE.format(e.getPlayer().getDisplayName(), getCurrentQuestion().getQuestionString(), getCurrentQuestion().getAnswerString(), getQuestionNum(), 0));
                playSound(e.getPlayer(), "Answer correct sound", "Answer correct pitch");
                scores.addScore(e.getPlayer());
                wasAnswered = true;
                timer.nextQuestion();

            }, 2L);
        }

    }

    private void playSound(Player player, String soundPath, String pitchPath) {
        String soundString = trivia.getConfig().getString(soundPath);

        try {
            float pitchVal = Float.parseFloat(Objects.requireNonNull(trivia.getConfig().getString(pitchPath, "1")));
            player.playSound(player.getLocation(), Sound.valueOf(soundString), 0.6f, pitchVal);
        } catch (IllegalArgumentException | NullPointerException exception) {
            if (soundString != null && !soundString.equalsIgnoreCase("none")) {
                switch (soundPath) {
                    case "Answer correct sound":
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6f, 1.5f);
                        break;
                    case "Time up sound":
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.6f, 0.9f);
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

}
