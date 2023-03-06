/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.effects;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class GameSound {

    private final TriviaTreasure triviaTreasure;
    private Sound soundVal;
    private float volumeVal;
    private float pitchVal;

    public GameSound(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
    }

    public void setSound(SoundType soundType) {
        String parentPath = soundType.getPath();

        try {
            String parseSoundValSetting = triviaTreasure.getConfig().getString(parentPath + ".sound");
            if (parseSoundValSetting == null || parseSoundValSetting.equalsIgnoreCase("none")) {
                soundVal = null;
                return;
            }
            soundVal = Sound.valueOf(parseSoundValSetting);
        } catch (IllegalArgumentException e) {
            soundVal = soundType.getDefaultSound();
        }

        try {
            volumeVal = (float) triviaTreasure.getConfig().getDouble(parentPath + ".volume", 1);
        } catch (Exception e) {
            volumeVal = 1;
        }

        try {
            pitchVal = (float) triviaTreasure.getConfig().getDouble(parentPath + ".pitch", 1);
        } catch (Exception e) {
            pitchVal = 0.6f;
        }
    }

    public void playSound(Player player, SoundType soundType) {
        setSound(soundType);
        if (soundVal != null) {
            player.playSound(player.getLocation(), soundVal, volumeVal, pitchVal);
        }
    }

    public void playSoundToAll(SoundType soundType) {
        setSound(soundType);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (soundVal != null) {
                player.playSound(player.getLocation(), soundVal, volumeVal, pitchVal);
            }
        }
    }


}

