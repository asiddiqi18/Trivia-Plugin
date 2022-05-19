/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;


import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

import java.util.function.Consumer;

// Timer class manages scheduling for trivia games
// for instance, round time, time between rounds

public class Timer implements Runnable {

    private final int rounds;
    private final long secondsPer;
    private final Consumer<Timer> everyRound;
    private final Runnable afterTimer;
    private final Trivia trivia;
    private final BossBar bossBar;
    private int counter;
    private int taskID;
    private int roundsLeft;


    public Timer(Trivia trivia, int rounds, long secondsPer, BossBar bossBar, Consumer<Timer> afterRound, Runnable afterGame) {
        this.trivia = trivia;
        this.rounds = rounds;
        this.roundsLeft = rounds;
        this.secondsPer = secondsPer;
        this.afterTimer = afterGame;
        this.everyRound = afterRound;
        this.bossBar = bossBar;
    }

    public int getRounds() {
        return rounds;
    }

    public int getRoundsLeft() {
        return roundsLeft;
    }

    // main loop for timer that executes every 100 ms
    // every 500 ms, update boss bar
    // every 1000ms * (seconds per round), go to next round
    @Override
    public void run() {
        counter += 1; // 100 ms
        if (counter % 5 == 0 && bossBar != null) {
            bossBar.setProgress(((rounds - roundsLeft - 1) + ((float) counter / (secondsPer * 10))) / rounds);
        }
        if (counter >= secondsPer * 10) {
            handleNextRound();
        }
    }

    public void handleNextRound() {
        // cancel current timer
        skipTimer();
        // check if no more rounds left
        if (roundsLeft < 1) {
            roundsLeft -= 1;
            afterTimer.run();
            return;
        }
        // schedule new timer
        counter = 0;
        roundsLeft -= 1;
        everyRound.accept(this);
    }

    // schedule new timer
    public void startTimer() {
        counter = 0;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(trivia, this, 10, 2);
    }


    public void skipTimer() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void endTimer() {
        roundsLeft = 0;
        handleNextRound();
    }

}
