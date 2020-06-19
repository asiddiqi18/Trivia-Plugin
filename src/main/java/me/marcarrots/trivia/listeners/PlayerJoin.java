package me.marcarrots.trivia.listeners;

import me.marcarrots.trivia.PlayerScoreHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private PlayerScoreHolder playerScoreHolder;

    public void setPlayerScoreHolder(PlayerScoreHolder playerScoreHolder) {
        this.playerScoreHolder = playerScoreHolder;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (playerScoreHolder == null) {
            return;
        }

        playerScoreHolder.updatePlayersToGame(event.getPlayer());

    }

}
