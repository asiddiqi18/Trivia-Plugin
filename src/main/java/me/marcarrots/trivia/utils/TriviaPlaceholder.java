/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.marcarrots.trivia.PlayerDataContainer;
import me.marcarrots.trivia.Trivia;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;

public class TriviaPlaceholder extends PlaceholderExpansion {

    private final Trivia trivia;

    public TriviaPlaceholder(Trivia trivia) {
        this.trivia = trivia;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Trivia";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MarCarrot";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.14";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        PlayerDataContainer stats = trivia.getStats();

        if (params.equalsIgnoreCase("game_wins_first")) {
            return String.valueOf(stats.getGamesWon(player)[0]);
        }
        if (params.equalsIgnoreCase("game_wins_second")) {
            return String.valueOf(stats.getGamesWon(player)[1]);
        }
        if (params.equalsIgnoreCase("game_wins_third")) {
            return String.valueOf(stats.getGamesWon(player)[2]);
        }

        if (params.equalsIgnoreCase("game_participation")) {
            return String.valueOf(stats.getGamesParticipated(player));
        }

        if (params.equalsIgnoreCase("round_wins")) {
            return String.valueOf(stats.getRoundsWon(player));
        }

        if (params.equalsIgnoreCase("money_won")) {
            return NumberFormat.getCurrencyInstance().format(stats.getMoneyWon(player));
        }

        if (params.equalsIgnoreCase("experience_won")) {
            return NumberFormat.getIntegerInstance().format(stats.getExperienceWon(player));
        }

        if (params.equalsIgnoreCase("game_in_progress")) {
            return String.valueOf(trivia.getGame() != null);
        }

        if (params.equalsIgnoreCase("next_game")) {
            if (trivia.getAutomatedGameManager().isSchedulingEnabled()) {
                return Elapsed.millisToElapsedTime(trivia.getAutomatedGameManager().getNextAutomatedTimeFromNow()).getElapsedFormattedString();
            } else {
                return "Scheduling is disabled";
            }
        }

        return null;
    }

}
