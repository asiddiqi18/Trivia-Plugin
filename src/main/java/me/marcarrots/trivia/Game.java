package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.listeners.PlayerJoin;
import me.marcarrots.trivia.menu.PlayerMenuUtility;
import me.marcarrots.trivia.utils.StringSimilarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getServer;

public class Game {

    private final QuestionHolder questionHolder;
    private final ChatEvent chatEvent;
    private final Trivia trivia;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final boolean doRepetition;
    private final PlayerScoreHolder scores;
    private final PlayerJoin playerJoin;
    private final double similarityScore;
    private final Player player;
    private Question currentQuestion;
    private Timer timer;
    private boolean wasAnswered;

    public Game(Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent, PlayerMenuUtility playerMenuUtility, PlayerJoin playerJoin) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.chatEvent = chatEvent;
        timePerQuestion = playerMenuUtility.getTimePer();
        amountOfRounds = playerMenuUtility.getTotalRounds();
        doRepetition = playerMenuUtility.isRepeatEnabled();
        player = playerMenuUtility.getOwner();
        similarityScore = trivia.getConfig().getDouble("Similarity score");
        scores = new PlayerScoreHolder(trivia);
        wasAnswered = true;
        this.playerJoin = playerJoin;
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

        chatEvent.setGame(this);
        playerJoin.setPlayerScoreHolder(scores);
        scores.addPlayersToGame();
        trivia.setGameActive(true);
        Bukkit.broadcastMessage(Lang.TRIVIA_START.format());
        timer = new Timer(trivia, amountOfRounds, timePerQuestion,
                () -> {
                },
                () -> {
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER.format());
                    Bukkit.broadcastMessage(Lang.TRIVIA_OVER_WINNER_LINE.format());
                    scores.broadcastLargestScores();
                    chatEvent.setGame(null);
                    trivia.setGameActive(false);
                    playerJoin.setPlayerScoreHolder(null);
                },
                (t) -> {
                    if (!wasAnswered) {
                        Bukkit.broadcastMessage(Lang.TIME_UP.format());
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

                scores.addScore(e.getPlayer());
                timer.nextQuestion();
                wasAnswered = true;

            }, 2L);
        }


    }

    public void stopGame() {
        timer.stop();
    }


}
