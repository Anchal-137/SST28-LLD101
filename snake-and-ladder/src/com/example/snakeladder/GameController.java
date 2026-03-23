package com.example.snakeladder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameController {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> players;
    private final List<Player> rankings;

    public GameController(Board board, List<Player> players) {
        this.board = board;
        this.dice = new Dice(6);
        this.players = new LinkedList<>(players);
        this.rankings = new ArrayList<>();
    }

    public void startGame() {
        board.displayBoard();
        System.out.println();

        while (players.size() > 1) {
            Player current = players.poll();

            int diceValue = dice.throwDice();
            int oldPosition = current.getPosition();
            int newPosition = board.resolveMove(oldPosition, diceValue);
            current.setPosition(newPosition);

            System.out.println(current.getName() + " rolled " + diceValue
                    + ": moved from " + oldPosition + " to " + newPosition);

            if (newPosition == board.getMaxCell()) {
                current.setFinished();
                rankings.add(current);
                System.out.println(current.getName() + " finished the race! (Rank #" + rankings.size() + ")");
            } else {
                players.add(current);
            }
        }

        if (!players.isEmpty()) {
            Player last = players.poll();
            System.out.println("\nMatch Complete! " + last.getName() + " is the last player remaining.");
        }

        System.out.println("\nFinal Rankings:");
        for (int i = 0; i < rankings.size(); i++) {
            System.out.println("  #" + (i + 1) + " " + rankings.get(i).getName());
        }
    }
}
