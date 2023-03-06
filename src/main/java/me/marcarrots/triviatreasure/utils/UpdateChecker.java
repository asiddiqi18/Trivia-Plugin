/*
 * Trivia by MarCarrot, 2020
 */
package me.marcarrots.triviatreasure.utils;

import me.marcarrots.triviatreasure.TriviaTreasure;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public final class UpdateChecker {

    private final TriviaTreasure triviaTreasure;
    private final int resourceId;

    public UpdateChecker(TriviaTreasure triviaTreasure, int resourceId) {
        this.triviaTreasure = triviaTreasure;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.triviaTreasure, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.triviaTreasure.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

}
