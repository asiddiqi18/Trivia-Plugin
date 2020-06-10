package me.marcarrots.trivia;

import java.util.Comparator;

public class PlayerScoreComparator implements Comparator<PlayerScore> {

    @Override
    public int compare(PlayerScore score1, PlayerScore score2) {
        return Integer.compare(score1.getPoints(), score2.getPoints());
    }
}
