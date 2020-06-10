package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        if (game == null) {
            Bukkit.getLogger().info("The game is null.");
            return;
        }

        game.playerAnswer(e);

    }


}
