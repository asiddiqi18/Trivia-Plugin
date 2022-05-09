/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.schedulers;

import me.marcarrots.trivia.Trivia;
import org.bukkit.Bukkit;

public class IntervalScheduler extends GameScheduler {

    private long nextAutomatedTimeEpoch;
    private int schedulerTask;

    public IntervalScheduler(Trivia trivia) {
        super(trivia);
    }

    public int getSchedulingType() {
        return schedulingType;
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
        if (schedulingType != 0) {
            return;
        }
        setNextAutomatedTimeEpoch();
        schedulerTask = trivia.getServer().getScheduler().scheduleSyncRepeatingTask(trivia, () -> {
            int onlinePlayerCount = Bukkit.getOnlinePlayers().size();
            if (onlinePlayerCount < automatedPlayerReq) {
                Bukkit.getLogger().info(String.format("Automated Trivia Canceled (%s players online, needed %s)...", onlinePlayerCount, automatedPlayerReq));
                setNextAutomatedTimeEpoch();
                return;
            }
            Bukkit.getLogger().info("Automated Trivia Beginning...");
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "trivia start");
            setNextAutomatedTimeEpoch();
        }, (long) automatedTimeMinutes * 20 * 60, (long) automatedTimeMinutes * 20 * 60);

    }

    public void cancel() {
        trivia.getServer().getScheduler().cancelTask(schedulerTask);
    }


}
