/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.schedulers;

import me.marcarrots.trivia.Trivia;

public abstract class GameScheduler {

    final Trivia trivia;
    final int schedulingType;
    final int automatedTimeMinutes;
    final int automatedPlayerReq;

    protected GameScheduler(Trivia trivia) {
        this.trivia = trivia;
        this.schedulingType = trivia.getConfig().getInt("Scheduled games", 0);
        this.automatedTimeMinutes = trivia.getConfig().getInt("Scheduled games interval", 60);
        this.automatedPlayerReq = trivia.getConfig().getInt("Scheduled games minimum players", 6);
    }

    public int getSchedulingType() {
        return schedulingType;
    }

    public int getAutomatedPlayerReq() {
        return automatedPlayerReq;
    }

    public abstract long getNextAutomatedTimeFromNow();

    public abstract void automatedSchedule();

    public abstract void cancel();



    }
