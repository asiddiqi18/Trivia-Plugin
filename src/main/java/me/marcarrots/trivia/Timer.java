/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia;


import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

import java.util.function.Consumer;

public class Timer implements Runnable {

    private int counter;
    private Trivia trivia;
    private int taskID;

    private final int rounds;
    private final long secondsPer;
    private final Consumer<Timer> everyRound;
    private final Runnable afterTimer;
    private int roundsLeft;
    private int activeTimers;
    private BossBar bossBar;


    public Timer(Trivia trivia, int rounds, long secondsPer, BossBar bossBar, Runnable afterTimer, Consumer<Timer> everyRound) {
        this.trivia = trivia;
        this.rounds = rounds;
        this.roundsLeft = rounds;
        this.secondsPer = secondsPer;
        this.afterTimer = afterTimer;
        this.everyRound = everyRound;
        this.bossBar = bossBar;
    }

    @Override
    public void run() {
        counter += 1; // 100 ms
        if (counter % 5 == 0) {
            bossBar.setProgress(((rounds - roundsLeft - 1) + ((float)counter / (secondsPer*10))) / rounds);
        }
        if (counter >= secondsPer*10) {
            handleNextRound();
        }
    }

    public void handleNextRound() {
        skipTimer();
        if (roundsLeft < 1) {
            afterTimer.run();
            System.out.println(activeTimers);
            return;
        }
        counter = 0;
        roundsLeft -= 1;
        everyRound.accept(this);
    }

    public void startTimer() {
        counter = 0;
        activeTimers += 1;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(trivia, this, 10, 2);
    }

    public void startTimerInitial() {
        handleNextRound();
    }

    public int getRounds() {
        return rounds;
    }

    public int getRoundsLeft() {
        return roundsLeft;
    }

    public void skipTimer() {
            activeTimers -= 1;
            Bukkit.getScheduler().cancelTask(taskID);
    }

    public void nextQuestion() {
        skipTimer();
        startTimerInitial();
    }

    public void endTimer() {
        roundsLeft = 0;
        nextQuestion();
    }

}
