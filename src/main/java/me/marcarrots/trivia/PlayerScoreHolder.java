package me.marcarrots.trivia;

import me.marcarrots.trivia.utils.Broadcaster;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.Placeholder;
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
            trivia.getStats().addGameParticipated(player);
            scores.put(player.getName(), new PlayerScore((player)));
        }
    }

    public void addPlayerToGame(Player player) {
        if (!scores.containsKey(player.getName())) {
            scores.put(player.getName(), new PlayerScore(player));
        }
    }

    public void addScore(Player player, int round) {
        PlayerScore score = scores.get(player.getName());
        score.incrementScore(round);
        scores.put(player.getName(), score);
    }

    public void deliverRewardsToWinners() {
        int displayAmount = Math.min(scores.size(), trivia.getConfig().getInt("Top winner amount", 3));

        List<PlayerScore> scoreValues = new ArrayList<>(scores.values());
        Collections.sort(scoreValues);

        String[] message = Lang.TRIVIA_WINNER_MESSAGE.format_multiple(new Placeholder.PlaceholderBuilder().build());
        List<String> winnerList = new ArrayList<>();

        if (scoreValues.size() == 0 || scoreValues.get(0).getPoints() == 0) {
            Broadcaster.broadcastMessage(Lang.TRIVIA_NO_WINNERS.format_multiple(null));
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
                winnerList.add(Lang.TRIVIA_ANNOUNCE_WINNER_LIST.format_single(new Placeholder.PlaceholderBuilder()
                        .player(player)
                        .points(score.getPoints())
                        .build()
                ));

                if (i < 3) {
                    rewards[i + 1].giveReward(player);
                }

            }
        }

        List<String> result = new ArrayList<>();
        for (String s : message) {
            if (s.contains("%winner_list%")) {
                result.addAll(winnerList);
            } else if (s.contains("%top_winner%")) {
                result.add(s.replace("%top_winner%", scoreValues.get(0).getPlayer().getDisplayName()));
            } else {
                result.add(s);
            }
        }

        String[] resultArray = new String[result.size()];
        Broadcaster.broadcastMessage(result.toArray(resultArray));


    }

}
