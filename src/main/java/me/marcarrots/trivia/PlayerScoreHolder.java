package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

public class PlayerScoreHolder {

    private final PriorityQueue<PlayerScore> scores;
    private final Trivia trivia;

    public PlayerScoreHolder(Trivia trivia) {
        scores = new PriorityQueue<>(new PlayerScoreComparator());
        this.trivia = trivia;
    }

    public void addPlayersToGame() {
        scores.clear();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            scores.add(new PlayerScore(player));
        }

    }

    public void updatePlayersToGame(Player player) {
        for (PlayerScore score : scores) {
            if (score.getPlayer().equals(player)) {
                return;
            }
        }
        scores.add(new PlayerScore(player));
    }

    public void addScore(Player player) {
        for (PlayerScore score : scores) {
            if (score.getPlayer() == player) {
                score.addScore();
            }
        }
    }

    public void broadcastLargestScores() {
        int displayAmount = Math.min(scores.size(), trivia.getConfig().getInt("Top winner amount", 3));
        if (scores.size() == 0 || scores.peek().getPoints() == 0) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "There are no winners of the trivia event!");
            return;
        }
        for (int i = 0; i < displayAmount; i++) {
            final PlayerScore score = scores.poll();
            if (score != null) {
                Bukkit.broadcastMessage(ChatColor.BLACK + "➤ " + ChatColor.DARK_AQUA + score.getPlayer().getDisplayName() + ": " + ChatColor.WHITE + score.getPoints());
            }
        }
    }


}
