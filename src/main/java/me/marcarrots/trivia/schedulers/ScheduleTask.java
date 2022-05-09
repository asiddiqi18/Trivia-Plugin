/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.schedulers;

import org.bukkit.Bukkit;

import java.util.TimerTask;

public class ScheduleTask extends TimerTask {
    private final int automatedPlayerReq;

    public ScheduleTask(int automatedPlayerReq) {
        this.automatedPlayerReq = automatedPlayerReq;
    }

    @Override
    public void run() {
        int onlinePlayerCount = Bukkit.getOnlinePlayers().size();
        if (onlinePlayerCount < automatedPlayerReq) {
            Bukkit.getLogger().info(String.format("Automated Trivia Canceled (%s players online, needed %s)...", onlinePlayerCount, automatedPlayerReq));
            return;
        }
        Bukkit.getLogger().info("Automated Trivia Beginning...");
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "trivia start");
    }
}
