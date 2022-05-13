/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.listeners;

import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Avoid fireworks from harming players.
 */
public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework firework = (Firework) event.getDamager();
            if (firework.hasMetadata("trivia-firework")) {
                event.setCancelled(true);
            }
        }
    }

}
