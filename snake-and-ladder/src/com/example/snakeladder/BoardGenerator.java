package com.example.snakeladder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BoardGenerator {

    public static Board generate(int size, GameDifficulty difficulty) {
        int maxCell = size * size;
        int maxEntities = Math.max(1, (maxCell - 2) / 5);
        int count = Math.min(size, maxEntities);
        Random random = new Random();
        Set<Integer> usedCells = new HashSet<>();
        usedCells.add(1);
        usedCells.add(maxCell);

        List<Snake> snakes = new ArrayList<>();
        List<Ladder> ladders = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int head = pickAvailableCell(random, usedCells, 2, maxCell - 1);
            usedCells.add(head);

            int tail = pickAvailableCell(random, usedCells, 1, head - 1);
            usedCells.add(tail);

            snakes.add(new Snake(head, tail));
        }

        for (int i = 0; i < count; i++) {
            int bottom = pickAvailableCell(random, usedCells, 2, maxCell - 1);
            usedCells.add(bottom);

            int top = pickAvailableCell(random, usedCells, bottom + 1, maxCell - 1);
            usedCells.add(top);

            ladders.add(new Ladder(bottom, top));
        }

        return new Board(size, snakes, ladders);
    }

    private static int pickAvailableCell(Random random, Set<Integer> used, int min, int max) {
        List<Integer> available = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            if (!used.contains(i)) {
                available.add(i);
            }
        }
        return available.get(random.nextInt(available.size()));
    }
}
