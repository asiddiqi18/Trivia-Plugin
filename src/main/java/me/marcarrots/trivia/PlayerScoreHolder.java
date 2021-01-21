package me.marcarrots.trivia;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

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
                    if (rewards == null) {
                        continue;
                    }
                    if (rewards[i].getMessage() != null) {
                        BukkitScheduler scheduler = getServer().getScheduler();
                        int finalI = i;
                        scheduler.scheduleSyncDelayedTask(trivia, () -> player.sendMessage(rewards[finalI].getMessage()), 3);
                    }
                    if (trivia.vaultEnabled()) {
                        EconomyResponse r = Trivia.getEcon().depositPlayer(score.getPlayer(), rewards[i].getMoney());
                        if (!r.transactionSuccess()) {
                            player.sendMessage(String.format("An error occurred: %s", r.errorMessage));
                        }
                    }
                    if (rewards[i].getExperience() != 0) {
                        player.giveExp(rewards[i].getExperience());
                    }
                    if (rewards[i].getItems() == null || rewards[i].getItems().size() == 0) {
                        continue;
                    }
                    for (ItemStack item : rewards[i].getItems()) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItem(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item);
                            player.updateInventory();
                        }
                    }

                }

            }
        }
    }

}
