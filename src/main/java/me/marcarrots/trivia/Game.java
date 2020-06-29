package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.utils.StringSimilarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getServer;

public class Game {

    private final QuestionHolder questionHolder;
    private final Trivia trivia;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;

    public PlayerScoreHolder getScores() {
        return scores;
    }

    private PlayerScoreHolder scores;
    private final double similarityScore;
    private final Player player;
    private Question currentQuestion;
    private Timer timer;
    private boolean wasAnswered;

    public Game(Trivia trivia, QuestionHolder questionHolder, PlayerMenuUtility playerMenuUtility) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        timePerQuestion = playerMenuUtility.getTimePer();
        amountOfRounds = playerMenuUtility.getTotalRounds();
        doRepetition = playerMenuUtility.isRepeatEnabled();
        player = playerMenuUtility.getOwner();
        similarityScore = trivia.getConfig().getDouble("Similarity score");
        scores = new PlayerScoreHolder(trivia);
        wasAnswered = true;
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
                () -> {
                },
                () -> {
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER.format());
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER_WINNER_LINE.format());
                    playSoundToAll("Game over sound", "Game over pitch");
                    scores.broadcastLargestScores();
                    scores = null;
                    trivia.clearGame();
                },
                (t) -> {
                    if (!wasAnswered) {
                        Bukkit.broadcastMessage(Lang.TIME_UP.format());
                        playSoundToAll("Time up sound", "Time up pitch");
                    }
                    wasAnswered = false;
                    setRandomQuestion();
                    Bukkit.broadcastMessage(Lang.QUESTION.format(null, getCurrentQuestion().getQuestionString(), getCurrentQuestion().getAnswerString(), getQuestionNum(), 0));
                }

        );

        timer.scheduleTimer();

    }


    private int getQuestionNum() {
        return Math.subtractExact(timer.getRounds() + 1, timer.getRoundsLeft());
    }


    public void playerAnswer(AsyncPlayerChatEvent e) {

        if (StringSimilarity.similarity(e.getMessage(), currentQuestion.getAnswerString()) >= similarityScore) {

            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(trivia, () -> {
                Bukkit.broadcastMessage(Lang.SOLVED_MESSAGE.format(e.getPlayer().getDisplayName(), getCurrentQuestion().getQuestionString(), getCurrentQuestion().getAnswerString(), getQuestionNum(), 0));
                playSound(e.getPlayer(), "Answer correct sound", "Answer correct pitch");
                scores.addScore(e.getPlayer());
                timer.nextQuestion();
                wasAnswered = true;

            }, 2L);
        }


    }

    private void playSound(Player player, String soundPath, String pitchPath) {
        String soundString = trivia.getConfig().getString(soundPath);

        try {
            float pitchVal = Float.parseFloat(trivia.getConfig().getString(pitchPath, "1"));
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
