/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.effects;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

public class Fireworks {

    public static void summonFireWork(Player player) {
        Random random = new Random();
        final FireworkEffect.Type[] fireWorkList = FireworkEffect.Type.values();
        FireworkEffect.Type type = fireWorkList[random.nextInt(fireWorkList.length)];
        Color color1 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color2 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        Color color3 = Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        Firework firework = player.getWorld().spawn(player.getLocation().add(0.5, 0.5, 0.5), Firework.class);
        firework.setMetadata("trivia-firework", new FixedMetadataValue(TriviaTreasure.getPlugin(TriviaTreasure.class), true));
        FireworkMeta fm = firework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().flicker(true).trail(true).with(type).withColor(color1).withColor(color2).withFade(color3).build());
        fm.setPower(3);

        firework.setFireworkMeta(fm);
    }

}
