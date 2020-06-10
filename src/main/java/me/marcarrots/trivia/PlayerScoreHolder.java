package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.PriorityQueue;

public class PlayerScoreHolder {

    private PriorityQueue<PlayerScore> scores;

    public PlayerScoreHolder() {
        scores = new PriorityQueue<>(new PlayerScoreComparator());
    }

    public void addPlayersToGame() {
        scores.clear();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            scores.add(new PlayerScore(player));
        }

    }

    public void updatePlayersToGame(Player player) {
        scores.add(new PlayerScore(player));
    }

    public void addScore(Player player) {
        for (PlayerScore score : scores) {
            if (score.getPlayer() == player) {
                score.addScore();
            }
        }
    }

    public void getLargestScores() {
        int displayAmount = Math.min(scores.size(), 3);
        for (int i = 0; i < displayAmount; i++) {
            final PlayerScore score = scores.poll();
            Bukkit.broadcastMessage(score.getPlayer().getDisplayName() + " " + score.getPoints());
        }
    }




}
