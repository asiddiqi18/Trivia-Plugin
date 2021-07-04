/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.language;

import java.awt.*;

public class Gradient {

    private final float red_diff;
    private final float green_diff;
    private final float blue_diff;
    private float red;
    private float green;
    private float blue;

    public Gradient(String base, String target, int intervals) {
        red = Integer.valueOf(base.substring(1, 3), 16);
        green = Integer.valueOf(base.substring(3, 5), 16);
        blue = Integer.valueOf(base.substring(5, 7), 16);

        float ref_red = Integer.valueOf(target.substring(1, 3), 16);
        float ref_green = Integer.valueOf(target.substring(3, 5), 16);
        float ref_blue = Integer.valueOf(target.substring(5, 7), 16);

        red_diff = (ref_red - red) / (intervals - 1);
        green_diff = (ref_green - green) / (intervals - 1);
        blue_diff = (ref_blue - blue) / (intervals - 1);
    }

    public void nextInterval() {
        red += red_diff;
        green += green_diff;
        blue += blue_diff;
    }

    public int getBlue() {
        return Math.round(blue);
    }

    public int getGreen() {
        return Math.round(green);
    }

    public int getRed() {
        return Math.round(red);
    }

    public Color toColor() {
        return new Color(getRed(), getGreen(), getBlue());
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", getRed(), getGreen(), getBlue());
    }

}