/*
 * Trivia by MarCarrot, 2020
 */
package me.marcarrots.trivia.api;

import me.marcarrots.trivia.Trivia;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public final class UpdateChecker {

    private Trivia trivia;
    private int resourceId;

    public UpdateChecker(Trivia trivia, int resourceId) {
        this.trivia = trivia;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.trivia, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.trivia.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }

}
