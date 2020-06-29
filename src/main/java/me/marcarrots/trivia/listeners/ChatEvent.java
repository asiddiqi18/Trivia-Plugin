package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.Game;
import me.marcarrots.trivia.Trivia;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    private final Trivia trivia;

    public ChatEvent(Trivia trivia) {
        this.trivia = trivia;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (trivia.getGame() == null) {
            return;
        }

        trivia.getGame().playerAnswer(event);

    }


}
