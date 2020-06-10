package me.marcarrots.trivia;

import me.marcarrots.trivia.listeners.ChatEvent;
import me.marcarrots.trivia.utils.StringSimilarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class Game {

    private final QuestionHolder questionHolder;
    private Question currentQuestion;
    private final ChatEvent chatEvent;
    private final Trivia trivia;
    private final long timePerQuestion;
    private final int amountOfRounds;
    private final PlayerScoreHolder scores;
    private Timer timer;
    private final double similarityScore;

    public Game(Trivia trivia, QuestionHolder questionHolder, ChatEvent chatEvent) {
        this.trivia = trivia;
        this.questionHolder = new QuestionHolder(questionHolder);
        this.chatEvent = chatEvent;
        timePerQuestion = 10;
        amountOfRounds = 4;
        similarityScore = trivia.getConfig().getDouble("Similarity score");
        scores = new PlayerScoreHolder();

    }

    public void setRandomQuestion() {
        currentQuestion = questionHolder.getRandomQuestion().getQuestionObj();
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void start(CommandSender sender) {

        if (questionHolder.getSize() == 0) {
            sender.sendMessage(ChatColor.RED + "There are no trivia questions loaded.");
            return;
        }

        if (questionHolder.getSize() < amountOfRounds) {
            sender.sendMessage("There are more rounds than questions, so questions will repeat.");
            questionHolder.setUniqueQuestions(false);
        } else {
            questionHolder.setUniqueQuestions(true);
        }

        chatEvent.setGame(this);
        scores.addPlayersToGame();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Trivia is commencing. Get ready!");
        timer = new Timer(trivia, this, amountOfRounds, timePerQuestion,
                () -> {},
                () -> {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Trivia is over!");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Winners:");
                    scores.getLargestScores();
                    chatEvent.setGame(null);

                },
                (t) -> {
                    setRandomQuestion();
                    Bukkit.broadcastMessage(ChatColor.GOLD + "(" + Math.subtractExact(timer.getRounds() + 1, timer.getRoundsLeft()) + ") " + ChatColor.YELLOW + getCurrentQuestion().getQuestionString());
                }

        );

        timer.scheduleTimer();

    }


    public void playerAnswer(AsyncPlayerChatEvent e) {

        if (StringSimilarity.similarity(e.getMessage(), currentQuestion.getAnswerString()) >= similarityScore) {

            BukkitScheduler scheduler = getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(trivia, () -> {
                Bukkit.broadcastMessage(e.getPlayer().getDisplayName() + ChatColor.GREEN + " has answered the question correctly! The answer was " + ChatColor.DARK_GREEN + currentQuestion.getAnswerString());
                scores.addScore(e.getPlayer());
                timer.nextQuestion();

            }, 2L);
        }


    }


}
