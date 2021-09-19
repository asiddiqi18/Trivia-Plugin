package me.marcarrots.trivia;

import org.bukkit.entity.Player;

public class PlayerScore implements Comparable<PlayerScore> {

    private Player player;
    private int points;

    public int getRoundLastScored() {
        return roundLastScored;
    }

    private int roundLastScored;

    public PlayerScore(Player player) {
        this.player = player;
        points = 0;
        roundLastScored = 0;
    }

    public void incrementScore(int round) {
        roundLastScored = round;
        points++;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    // returning 0 -> object considered greater
    // determined by points first, if points are equal, then compares who got to those points first
    @Override
    public int compareTo(PlayerScore o) {
        int scoreCompare = Integer.compare(o.getPoints(), this.getPoints()); // returns greater than 0 if other is greater
        if (scoreCompare == 0) {
            return Integer.compare(this.getRoundLastScored(), o.getRoundLastScored()); // returns greater than 0 if other is less
        } else {
            return scoreCompare;
        }
    }
}
