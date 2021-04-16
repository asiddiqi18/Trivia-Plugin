package me.marcarrots.trivia;

import me.marcarrots.trivia.Language.Lang;
import me.marcarrots.trivia.Language.LangBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayerScoreHolder {

    private final List<PlayerScore> scores;
    private final Trivia trivia;

    public PlayerScoreHolder(Trivia trivia) {
        scores = new ArrayList<>();
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
            if (score.getPlayer().getName().equals(player.getName())) {
                score.setPlayer(player);
                return;
            }
        }
        scores.add(new PlayerScore(player));
    }

    public void addScore(Player player) {
        for (PlayerScore score : scores) {
            if (score.getPlayer().getName().equals(player.getName())) {
                score.addScore();
            }

        }
    }

    public void broadcastLargestScores() {
        int displayAmount = Math.min(scores.size(), trivia.getConfig().getInt("Top winner amount", 3));
        Collections.sort(scores);
        if (scores.size() == 0 || scores.get(0).getPoints() == 0) {
            Bukkit.broadcastMessage(Lang.TRIVIA_NO_WINNERS.format(null));
            return;
        }
        for (int i = 0; i < displayAmount; i++) {
            final PlayerScore score = scores.get(i);
            if (score != null) {
                if (score.getPoints() == 0) { // don't show winners if they didn't score at all.
                    break;
                }
                Player player = score.getPlayer();
                Rewards[] rewards = trivia.getRewards();
                Bukkit.broadcastMessage(Lang.TRIVIA_ANNOUNCE_WINNER_LIST.format(new LangBuilder()
                        .setPlayer(player)
                        .setPoints(score.getPoints())
                ));

                if (trivia.getConfig().getBoolean("Summon fireworks", true)) {
                    Effects.summonFireWork(player);
                }

                if (i < 3) {
                    rewards[i + 1].giveReward(player);
                }

            }
        }
    }

}
