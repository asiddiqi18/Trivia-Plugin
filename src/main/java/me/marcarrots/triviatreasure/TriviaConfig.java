package me.marcarrots.triviatreasure;

import org.bukkit.configuration.file.FileConfiguration;

public class TriviaConfig {

    public final double similarityScore;
    public final int timeBetweenRounds;
    public final boolean bossBarEnabled;
    public final int defaultRounds;
    public final long defaultTimePer;
    public final int topWinnerAmount;

    public TriviaConfig(FileConfiguration config) {
        similarityScore = config.getDouble("Similarity score", 0.85);
        timeBetweenRounds = config.getInt("Time between rounds", 2);
        bossBarEnabled = config.getBoolean("Enable boss bar", true);
        defaultRounds = config.getInt("Default rounds", 10);
        defaultTimePer = config.getLong("Default time per round", 10L);
        topWinnerAmount = config.getInt("Top winner amount", 3);
    }
}
