package com.example.snakeladder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter board size (n for n x n board): ");
        int n = scanner.nextInt();

        System.out.print("Enter number of players: ");
        int playerCount = scanner.nextInt();

        System.out.print("Enter difficulty level (easy/hard): ");
        String diffInput = scanner.next().trim().toUpperCase();
        DifficultyLevel difficulty = DifficultyLevel.valueOf(diffInput);

        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = scanner.next();
            players.add(new Player(name));
        }

        Board board = BoardFactory.createBoard(n, difficulty);
        GameEngine engine = new GameEngine(board, players);

        System.out.println("\n--- Game Start ---\n");
        engine.play();

        scanner.close();
    }
}
