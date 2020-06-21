package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

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
        Collections.sort(scores);
        if (scores.size() == 0 || scores.get(0).getPoints() == 0) {
            Bukkit.broadcastMessage(Lang.TRIVIA_NO_WINNERS.format());
            return;
        }
        for (int i = 0; i < displayAmount; i++) {
            final PlayerScore score = scores.get(i);
            if (score != null) {
                Bukkit.broadcastMessage(Lang.TRIVIA_ANNOUNCE_WINNER_LIST.format(score.getPlayer().getDisplayName(), null, null, 0, score.getPoints()));
                if (trivia.getConfig().getBoolean("Summon fireworks", true)) {
                    summonFireWork(score.getPlayer());
                }
            }
        }
    }

    private void summonFireWork(Player player) {
        Random random = new Random();
        final FireworkEffect.Type[] fireWorkList = FireworkEffect.Type.values();
        FireworkEffect.Type type = fireWorkList[random.nextInt(fireWorkList.length)];
        Color color1 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color2 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color3 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));


        Firework firework = player.getWorld().spawn(player.getLocation().add(0.5, 0.5, 0.5), Firework.class);
        FireworkMeta fm = firework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(type).withColor(color1).withColor(color2).withFade(color3).build());
        fm.setPower(3);
        firework.setFireworkMeta(fm);
    }

}
