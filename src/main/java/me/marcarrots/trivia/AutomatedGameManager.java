/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class AutomatedGameManager {

    private final Trivia trivia;
    private final boolean schedulingEnabled;
    private final int automatedTime;
    private final int automatedPlayerReq;
    private long nextAutomatedTime;
    private int schedulerTask;

    public AutomatedGameManager(Trivia trivia) {
        this.trivia = trivia;
        schedulingEnabled = trivia.getConfig().getBoolean("Scheduled games", false);
        automatedTime = trivia.getConfig().getInt("Scheduled games interval", 60);
        automatedPlayerReq = trivia.getConfig().getInt("Scheduled games minimum players", 6);
    }


    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }


    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
    }


    public long getNextAutomatedTime() {
        return nextAutomatedTime;
    }


    private void setNextAutomatedTime() {
        nextAutomatedTime = System.currentTimeMillis() + ((long) automatedTime * 60 * 1000);
    }


    public void automatedSchedule() {
        if (!schedulingEnabled) {
            return;
        }
        setNextAutomatedTime();
        schedulerTask = trivia.getServer().getScheduler().scheduleSyncRepeatingTask(trivia, () -> {
            if (Bukkit.getOnlinePlayers().size() < automatedPlayerReq) {
                return;
            }
            Bukkit.getLogger().info("Automated Trivia Beginning...");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "trivia start");
            setNextAutomatedTime();
        }, (long) automatedTime * 20 * 60, (long) automatedTime * 20 * 60);

    }

    public void cancel() {
        trivia.getServer().getScheduler().cancelTask(schedulerTask);
    }


}
