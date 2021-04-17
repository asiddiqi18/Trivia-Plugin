package me.marcarrots.trivia;

import me.marcarrots.trivia.Language.Lang;
import me.marcarrots.trivia.Language.LangBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerScoreHolder {

    private final HashMap<String, PlayerScore> scores;
    private final Trivia trivia;

    public PlayerScoreHolder(Trivia trivia) {
        scores = new HashMap<>();
        this.trivia = trivia;
    }

    public void addOnlinePlayersToGame() {
        scores.clear();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            scores.put(player.getName(), new PlayerScore((player)));
        }
    }

    public void addPlayerToGame(Player player) {
        if (!scores.containsKey(player.getName())) {
            scores.put(player.getName(), new PlayerScore(player));
        }
    }

    public void addScore(Player player) {
        PlayerScore score = scores.get(player.getName());
        score.incrementScore();
        scores.put(player.getName(), score);
    }

    public void broadcastLargestScores() {
        int displayAmount = Math.min(scores.size(), trivia.getConfig().getInt("Top winner amount", 3));

        List<PlayerScore> scoreValues = new ArrayList<>(scores.values());
        Collections.sort(scoreValues);

        if (scoreValues.size() == 0 || scoreValues.get(0).getPoints() == 0) {
            Bukkit.broadcastMessage(Lang.TRIVIA_NO_WINNERS.format(null));
            return;
        }
        for (int i = 0; i < displayAmount; i++) {
            final PlayerScore score = scoreValues.get(i);
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
