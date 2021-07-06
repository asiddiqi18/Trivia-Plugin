/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.Random;

public class Effects {

    public static void summonFireWork(Player player) {
        Random random = new Random();
        final FireworkEffect.Type[] fireWorkList = FireworkEffect.Type.values();
        FireworkEffect.Type type = fireWorkList[random.nextInt(fireWorkList.length)];
        Color color1 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color2 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color3 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Firework firework = player.getWorld().spawn(player.getLocation().add(0.5, 0.5, 0.5), Firework.class);
        firework.setMetadata("trivia-firework", new FixedMetadataValue(Trivia.getPlugin(Trivia.class), true));
        FireworkMeta fm = firework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(type).withColor(color1).withColor(color2).withFade(color3).build());
        fm.setPower(3);

        firework.setFireworkMeta(fm);
    }

    public static void playSound(Player player, FileConfiguration configuration, String soundPath, String pitchPath) {
        String soundString = configuration.getString(soundPath);
        try {
            float pitchVal = Float.parseFloat(Objects.requireNonNull(configuration.getString(pitchPath, "1")));
            player.playSound(player.getLocation(), Sound.valueOf(soundString), 0.6F, pitchVal);
        } catch (IllegalArgumentException | NullPointerException exception) {
            if (soundString != null && !soundString.equalsIgnoreCase("none")) {
                switch (soundPath) {
                    case "Answer correct sound":
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.5F);
                        break;
                    case "Time up sound":
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.6F, 0.9F);
                        break;
                }
            }
        }
    }

    public static void playSoundToAll(String path, FileConfiguration configuration, String pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            playSound(player, configuration, path, pitch);
        }
    }

}
