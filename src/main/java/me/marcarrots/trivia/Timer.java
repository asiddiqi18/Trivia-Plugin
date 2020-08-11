package me.marcarrots.trivia;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class Timer implements Runnable {

    // Main class for Bukkit scheduling
    private final JavaPlugin plugin;
    private final int rounds;

    // Seconds
    private final long secondsPer;
    private final Consumer<Timer> everySecond;
    // Actions to perform while counting down, before and after
    private final Runnable afterTimer;
    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;
    private int roundsLeft;
    // Construct a timer, you could create multiple so for example if
    // you do not want these "actions"

    public Timer(JavaPlugin plugin, int rounds, long secondsPer,
                 Runnable afterTimer,
                 Consumer<Timer> everySecond) {
        // Initializing fields
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
        int timeBetween = 0;
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, timeBetween, secondsPer * 20);
    }

    public void skipTimer() {
        if (assignedTaskId != null) {
            Bukkit.getScheduler().cancelTask(assignedTaskId);
        }
    }

    public void nextQuestion() {
        skipTimer();
        scheduleTimer();
    }


}
