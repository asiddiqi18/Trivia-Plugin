package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.Trivia;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final Trivia trivia;

    public PlayerJoin(Trivia trivia) {
        this.trivia = trivia;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (trivia.getGame() == null) {
            return;
        }

        trivia.getGame().getScores().updatePlayersToGame(event.getPlayer());

    }

}
