/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.utils;

public class Elapsed {

    private final long elapsedDays;
    private final long elapsedHours;
    private final long elapsedMinutes;
    private final long elapsedSeconds;

    private Elapsed(long elapsedDays, long elapsedHours, long elapsedMinutes, long elapsedSeconds) {
        this.elapsedDays = elapsedDays;
        this.elapsedHours = elapsedHours;
        this.elapsedMinutes = elapsedMinutes;
        this.elapsedSeconds = elapsedSeconds;
    }

    public static Elapsed millisToElapsedTime(long millis) {

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = millis / daysInMilli;
        millis = millis % daysInMilli;

        long elapsedHours = millis / hoursInMilli;
        millis = millis % hoursInMilli;

        long elapsedMinutes = millis / minutesInMilli;
        millis = millis % minutesInMilli;

        long elapsedSeconds = millis / secondsInMilli;

        return new Elapsed(elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public long getElapsedDays() {
        return elapsedDays;
    }

    public long getElapsedHours() {
        return elapsedHours;
    }

    public long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public String getElapsedFormattedString() {

        long[] elapsedUnits = {elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
        String[] elapsedUnitsString = {"day", "hour", "minute", "second"};
        StringBuilder sb = new StringBuilder();
        String commaSep = "";

        for (int i = 0; i < elapsedUnits.length; i++) {
            if (elapsedUnits[i] != 0) {
                sb.append(String.format("%s%d " + elapsedUnitsString[i], commaSep, elapsedUnits[i]));
                if (elapsedUnits[i] > 1) {
                    sb.append("s");
                }
                commaSep = ", ";
            }
        }

        if (commaSep.equals("")) {
            return "0 seconds";
        } else {
            return sb.toString();
        }
    }


}
