package me.marcarrots.triviatreasure;

import org.bukkit.command.CommandSender;

public class GameManager {

    private final TriviaTreasure triviaTreasure;
    private Game game;

    public GameManager(TriviaTreasure triviaTreasure) {
        this.triviaTreasure = triviaTreasure;
    }

    public void startGame(CommandSender commandSender, long timePerQuestion, int rounds, boolean doRepetition) {
        game = new Game(triviaTreasure, commandSender, timePerQuestion, rounds, doRepetition);
        game.start();
    }

    public void stopGame() {
        if (game != null) {
            game.stop();
        }
    }

    public boolean isGameRunning() {
        return game != null;
    }

    public Game getGame() {
        return game;
    }

    public void clearGame() {
        game = null;
    }
}
