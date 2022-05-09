/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.schedulers;

import me.marcarrots.trivia.Trivia;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimeOfDayScheduler extends GameScheduler{

    public TimeOfDayScheduler(Trivia trivia) {
        super(trivia);
    }

    @Override
    public long getNextAutomatedTimeFromNow() {
        return 0;
    }

    @Override
    public void automatedSchedule() {

    }

    @Override
    public void cancel() {

    }

    public void automateSchedule() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        // every night at 2am you run your task
        Timer timer = new Timer();
        timer.schedule(new ScheduleTask(automatedPlayerReq), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day


    }

}
