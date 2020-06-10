package me.marcarrots.trivia;

import org.bukkit.entity.Player;

public class PlayerScore {

    private Player player;

    private int points;

    public PlayerScore(Player player) {
        this.player = player;
        points = 0;
    }

    public void addScore() {
        points++;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPoints() {
        return points;
    }


}
