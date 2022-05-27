/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.utils;

import org.bukkit.Bukkit;

public class Broadcaster {

    public static void broadcastMessage(String[] message) {
        for (String s : message) {
            broadcastMessage(s);
        }
    }

    public static void broadcastMessage(String message) {
        if (message.equals("")) {
            return;
        }
        Bukkit.broadcastMessage(message);
    }
}
