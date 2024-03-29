/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("SameParameterValue")
public class PlayerDataContainer {

    private final NamespacedKey participatedKey;
    private final NamespacedKey roundsWonKey;
    private final NamespacedKey gamesWonKey;
    private final NamespacedKey moneyWonKey;
    private final NamespacedKey experienceWonKey;

    public PlayerDataContainer(TriviaTreasure triviaTreasure) {
        gamesWonKey = new NamespacedKey(triviaTreasure, "trivia_wins");
        roundsWonKey = new NamespacedKey(triviaTreasure, "trivia_answered");
        moneyWonKey = new NamespacedKey(triviaTreasure, "trivia_money");
        participatedKey = new NamespacedKey(triviaTreasure, "trivia_participation");
        experienceWonKey = new NamespacedKey(triviaTreasure, "trivia_experience");
    }

    public void addGameParticipated(Player player) {
        addIntegerType(player, participatedKey, 1);
    }

    public void addRoundWon(Player player) {
        addIntegerType(player, roundsWonKey, 1);
    }

    public void addGamesWon(Player player, int position) {
        addArrayType(player, gamesWonKey, 1, position - 1, 3);
    }

    public void addMoneyWon(Player player, double amount) {
        addDoubleType(player, moneyWonKey, amount);
    }

    public void addExperienceWon(Player player, int amount) {
        addIntegerType(player, experienceWonKey, amount);
    }

    public int getGamesParticipated(Player player) {
        return getIntegerType(player, participatedKey);
    }

    public int getRoundsWon(Player player) {
        return getIntegerType(player, roundsWonKey);
    }

    public int[] getGamesWon(Player player) {
        return getArrayType(player, gamesWonKey, 3);
    }

    public double getMoneyWon(Player player) {
        return getDoubleType(player, moneyWonKey);
    }

    public int getExperienceWon(Player player) {
        return getIntegerType(player, experienceWonKey);
    }

    @SuppressWarnings("SameParameterValue")
    private void addIntegerType(Player player, NamespacedKey key, int amount) {
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, getIntegerType(player, key) + amount);
    }

    private void addDoubleType(Player player, NamespacedKey key, double amount) {
        player.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, getDoubleType(player, key) + amount);
    }

    private void addArrayType(Player player, NamespacedKey key, int amount, int index, int size) {
        int[] arr = getArrayType(player, key, size);
        arr[index] += amount;
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, arr);
    }

    private int getIntegerType(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0);
    }

    private double getDoubleType(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.DOUBLE, 0d);
    }

    private int[] getArrayType(Player player, NamespacedKey key, int size) {
        int[] arr = new int[size];
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER_ARRAY, arr);
    }


}
