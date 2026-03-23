package com.example.snakeladder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameEngine {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> players;
    private final List<Player> winners;

    public GameEngine(Board board, List<Player> players) {
        this.board = board;
        this.dice = new Dice(6);
        this.players = new LinkedList<>(players);
        this.winners = new ArrayList<>();
    }

    public void play() {
        board.printBoard();
        System.out.println();

        while (players.size() > 1) {
            Player current = players.poll();

            int diceValue = dice.roll();
            int oldPosition = current.getPosition();
            int newPosition = board.movePlayer(oldPosition, diceValue);
            current.setPosition(newPosition);

            System.out.println(current.getName() + " rolled " + diceValue
                    + ": moved from " + oldPosition + " to " + newPosition);

            if (newPosition == board.getMaxCell()) {
                current.markWon();
                winners.add(current);
                System.out.println(current.getName() + " has WON! (Rank #" + winners.size() + ")");
            } else {
                players.add(current);
            }
        }

        if (!players.isEmpty()) {
            Player last = players.poll();
            System.out.println("\nGame Over! " + last.getName() + " is the last player remaining.");
        }

        System.out.println("\nFinal Rankings:");
        for (int i = 0; i < winners.size(); i++) {
            System.out.println("  #" + (i + 1) + " " + winners.get(i).getName());
        }
    }
}
