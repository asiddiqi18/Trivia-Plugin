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
    private final Runnable beforeTimer;
    // Actions to perform while counting down, before and after
    private final Runnable afterTimer;
    // Our scheduled task's assigned id, needed for canceling
    private Integer assignedTaskId;
    private int roundsLeft;
    // Construct a timer, you could create multiple so for example if
    // you do not want these "actions"

    public Timer(JavaPlugin plugin, int rounds, long secondsPer,
                 Runnable beforeTimer, Runnable afterTimer,
                 Consumer<Timer> everySecond) {
        // Initializing fields
        this.plugin = plugin;

        this.rounds = rounds;
        this.roundsLeft = rounds;
        this.secondsPer = secondsPer;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    public void stop() {
        roundsLeft = rounds;
        nextQuestion();
    }

    @Override
    public void run() {
        // Is the timer up?
        if (roundsLeft < 1) {
            // Do what was supposed to happen after the timer
            afterTimer.run();

            // Cancel timer
            if (assignedTaskId != null) {
                Bukkit.getScheduler().cancelTask(assignedTaskId);
            }
            return;
        }

        // Are we just starting?
        if (roundsLeft == rounds) {
            beforeTimer.run();
        }

        // Do what's supposed to happen every second
        everySecond.accept(this);

        // Decrement the seconds left
        roundsLeft--;
    }

    public int getRounds() {
        return rounds;
    }

    /**
     * Gets the total seconds this timer was set to run for
     *
     * @return Total seconds timer should run
     */
    public int getTotalSeconds() {
        return rounds;
    }

    /**
     * Gets the seconds left this timer should run
     *
     * @return Seconds left timer should run
     */
    public int getRoundsLeft() {
        return roundsLeft;
    }

    /**
     * Schedules this instance to "run" every second
     */
    public void scheduleTimer() {
        // Initialize our assigned task's id, for later use so we can cancel
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 10, secondsPer * 20);
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
