/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure;

import org.bukkit.Bukkit;

public class AutomatedGameManager {

    private final TriviaTreasure triviaTreasure;
    private final boolean schedulingEnabled;
    private final int automatedTimeMinutes;
    private final int automatedPlayerReq;
    private long nextAutomatedTimeEpoch;
    private int schedulerTask;

    public AutomatedGameManager(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
        schedulingEnabled = triviaTreasure.getConfig().getBoolean("Scheduled games", false);
        automatedTimeMinutes = triviaTreasure.getConfig().getInt("Scheduled games interval", 60);
        automatedPlayerReq = triviaTreasure.getConfig().getInt("Scheduled games minimum players", 6);
    }

    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
    }

    public long getNextAutomatedTimeFromNow() {
        return nextAutomatedTimeEpoch - System.currentTimeMillis();
    }

    private void setNextAutomatedTimeEpoch() {
        nextAutomatedTimeEpoch = System.currentTimeMillis() + ((long) automatedTimeMinutes * 60 * 1000);
    }

    public void automatedSchedule() {
        if (!schedulingEnabled) {
            return;
        }
        setNextAutomatedTimeEpoch();
        schedulerTask = triviaTreasure.getServer().getScheduler().scheduleSyncRepeatingTask(triviaTreasure, () -> {
            int onlinePlayerCount = Bukkit.getOnlinePlayers().size();
            if (onlinePlayerCount < automatedPlayerReq) {
                triviaTreasure.getLogger().info(String.format("Automated Trivia Canceled (%s players online, needed %s)...", onlinePlayerCount, automatedPlayerReq));
                setNextAutomatedTimeEpoch();
                return;
            }
            triviaTreasure.getLogger().info("Automated Trivia Beginning...");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "trivia start");
            setNextAutomatedTimeEpoch();
        }, (long) automatedTimeMinutes * 20 * 60, (long) automatedTimeMinutes * 20 * 60);

    }

    public void cancel() {
        triviaTreasure.getServer().getScheduler().cancelTask(schedulerTask);
    }


}
