package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class Timer implements Runnable {

    private final JavaPlugin plugin;
    private final int rounds;
    private final long secondsPer;
    private final Consumer<Timer> everySecond;
    private final Runnable afterTimer;

    public Integer getAssignedTaskId() {
        return assignedTaskId;
    }

    private Integer assignedTaskId;
    private int roundsLeft;

    public Timer(JavaPlugin plugin, int rounds, long secondsPer, Runnable afterTimer, Consumer<Timer> everySecond) {
        this.plugin = plugin;
        this.rounds = rounds;
        this.roundsLeft = rounds;
        this.secondsPer = secondsPer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {
        if (roundsLeft < 1) {
            afterTimer.run();
            if (assignedTaskId != null) {
                Bukkit.getScheduler().cancelTask(assignedTaskId);
            }
            return;
        }

        everySecond.accept(this);
        roundsLeft--;
    }

    public void stop() {
        roundsLeft = 0;
        nextQuestion();
    }

    public int getRounds() {
        return rounds;
    }

    public int getRoundsLeft() {
        return roundsLeft;
    }

    public void scheduleTimer() {
        assignedTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, secondsPer * 20);
    }

    public void scheduleTimerInitialize() {
        assignedTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this, 1);
    }

    public void skipTimer() {
        if (assignedTaskId != null) {
            Bukkit.getScheduler().cancelTask(assignedTaskId);
        }
    }

    public void nextQuestion() {
        skipTimer();
        run();
    }

}
