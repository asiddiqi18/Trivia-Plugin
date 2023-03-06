package me.marcarrots.triviatreasure.listeners;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final TriviaTreasure triviaTreasure;

    public PlayerJoin(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (event.getPlayer().hasPermission("trivia.admin")) {
            if (triviaTreasure.getUpdateNotice() != null) {
                event.getPlayer().sendMessage(triviaTreasure.getUpdateNotice());
            }
        }

        if (triviaTreasure.getGame() == null) {
            return;
        }

        triviaTreasure.getGame().getGameBossBar().showBarToPlayer(event.getPlayer());
        triviaTreasure.getGame().getScores().addPlayerToGame(event.getPlayer());

    }

}
