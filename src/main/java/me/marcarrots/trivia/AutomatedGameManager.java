/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import org.bukkit.Bukkit;

public class AutomatedGameManager {

    private final Trivia trivia;
    private final boolean schedulingEnabled;
    private final int automatedTimeMinutes;
    private final int automatedPlayerReq;
    private long nextAutomatedTimeEpoch;
    private int schedulerTask;

    public AutomatedGameManager(Trivia trivia) {
        this.trivia = trivia;
        schedulingEnabled = trivia.getConfig().getBoolean("Scheduled games", false);
        automatedTimeMinutes = trivia.getConfig().getInt("Scheduled games interval", 60);
        automatedPlayerReq = trivia.getConfig().getInt("Scheduled games minimum players", 6);
    }


    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }


    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
    }


    private long getNextAutomatedTimeEpoch() {
        return nextAutomatedTimeEpoch;
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
        schedulerTask = trivia.getServer().getScheduler().scheduleSyncRepeatingTask(trivia, () -> {
            int onlinePlayerCount = Bukkit.getOnlinePlayers().size();
            if (onlinePlayerCount < automatedPlayerReq) {
                trivia.getLogger().info(String.format("Automated Trivia Canceled (%s players online, needed %s)...", onlinePlayerCount, automatedPlayerReq));
                setNextAutomatedTimeEpoch();
                return;
            }
            trivia.getLogger().info("Automated Trivia Beginning...");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "trivia start");
            setNextAutomatedTimeEpoch();
        }, (long) automatedTimeMinutes * 20 * 60, (long) automatedTimeMinutes * 20 * 60);

    }

    public void cancel() {
        trivia.getServer().getScheduler().cancelTask(schedulerTask);
    }


}
