/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.effects;

import org.bukkit.Sound;

public enum SoundType {

    CORRECT("Answer correct", Sound.BLOCK_NOTE_BLOCK_PLING),
    TIME_UP("Time up", Sound.ENTITY_VILLAGER_NO),
    GAME_START("Game start", Sound.ENTITY_PLAYER_LEVELUP),
    GAME_OVER("Game over", Sound.ENTITY_PLAYER_LEVELUP), QUESTION_SKIPPED("Question skipped", Sound.BLOCK_ANVIL_BREAK);

    private final String path;
    private final Sound defaultSound;

    SoundType(String path, Sound defaultSound) {
        this.path = path;
        this.defaultSound = defaultSound;
    }

    public String getPath() {
        return path;
    }

    public Sound getDefaultSound() {
        return defaultSound;
    }
}
