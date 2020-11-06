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

        if (event.getPlayer().hasPermission("trivia.admin")) {
            if (trivia.getUpdateNotice() != null) {
                event.getPlayer().sendMessage(trivia.getUpdateNotice());
            }
        }

        if (trivia.getGame() == null) {
            return;
        }

        trivia.getGame().showBarToPlayer(event.getPlayer());
        trivia.getGame().getScores().updatePlayersToGame(event.getPlayer());

    }

}
