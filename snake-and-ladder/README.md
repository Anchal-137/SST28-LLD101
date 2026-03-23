# Snake and Ladder - Low Level Design

A snake and ladder game built in Java with configurable board size, difficulty levels, and multiplayer support.

## Features

- Configurable N x N board size
- Difficulty levels (EASY, HARD) with auto-generated snakes and ladders
- Multiplayer support with turn-based gameplay
- Ranking system for finished players
- Random board generation via Factory Pattern

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     SNAKE AND LADDER - CLASS DIAGRAM                     │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────────────┐
    │      <<enum>>        │
    │   GameDifficulty     │
    ├──────────────────────┤
    │  EASY                │
    │  HARD                │
    └──────────┬───────────┘
               │
               │ uses
               ▼
    ┌───────────────────────────────────────────┐
    │          BoardGenerator                    │
    ├───────────────────────────────────────────┤
    │+ generate(size, difficulty): Board         │  (static)
    │- pickAvailableCell(random, used,           │
    │                    min, max): int          │  (static)
    └─────────────────────┬─────────────────────┘
                          │
                          │ creates
                          ▼
    ┌──────────────────────────────────────────┐
    │               Board                       │
    ├──────────────────────────────────────────┤
    │- size: int                                │
    │- maxCell: int                             │
    │- snakePositions: Map<Integer, Integer>    │
    │- ladderPositions: Map<Integer, Integer>   │
    ├──────────────────────────────────────────┤
    │+ getMaxCell(): int                        │
    │+ resolveMove(currentPos, diceVal): int    │
    │+ isCellTaken(cell): boolean               │
    │+ displayBoard(): void                     │
    └────────┬──────────────────┬───────────────┘
             │                  │
             │ has many         │ has many
             ▼                  ▼
    ┌─────────────────┐  ┌──────────────────┐
    │     Snake        │  │     Ladder       │
    ├─────────────────┤  ├──────────────────┤
    │- start: int      │  │- bottom: int     │
    │- end: int        │  │- top: int        │
    ├─────────────────┤  ├──────────────────┤
    │+ getStart(): int │  │+ getBottom(): int│
    │+ getEnd(): int   │  │+ getTop(): int   │
    │+ toString()      │  │+ toString()      │
    └─────────────────┘  └──────────────────┘

    ┌──────────────────────┐
    │        Dice           │
    ├──────────────────────┤
    │- sides: int           │
    │- random: Random       │
    ├──────────────────────┤
    │+ throwDice(): int     │
    └──────────┬───────────┘
               │
               │ used by
               ▼
    ┌──────────────────────────────────────────────┐
    │              GameController                    │
    ├──────────────────────────────────────────────┤
    │- board: Board                                 │
    │- dice: Dice                                   │
    │- players: Queue<Player>                       │
    │- rankings: List<Player>                       │
    ├──────────────────────────────────────────────┤
    │+ startGame(): void                            │
    └──────────────────────┬───────────────────────┘
                           │
                           │ manages
                           ▼
    ┌──────────────────────────────┐
    │          Player              │
    ├──────────────────────────────┤
    │- name: String                │
    │- position: int               │
    │- finished: boolean           │
    ├──────────────────────────────┤
    │+ getName(): String           │
    │+ getPosition(): int          │
    │+ setPosition(int): void      │
    │+ isFinished(): boolean       │
    │+ setFinished(): void         │
    │+ toString(): String          │
    └──────────────────────────────┘

                    ┌──────────────────┐
                    │       App        │
                    ├──────────────────┤
                    │+ main(args)      │  (entry point)
                    └──────────────────┘
                      │  uses: BoardGenerator, GameController, Player
```

## Relationships Summary

| Relationship | Type |
|---|---|
| `GameController` --> `Board` | **composition** (has-a) |
| `GameController` --> `Dice` | **composition** (has-a) |
| `GameController` --> `Player` | **manages** (Queue + rankings List) |
| `Board` --> `Snake` | **aggregation** (has many) |
| `Board` --> `Ladder` | **aggregation** (has many) |
| `BoardGenerator` --> `Board` | **creates** (Factory Pattern) |
| `BoardGenerator` --> `GameDifficulty` | **uses** (enum) |
| `App` --> `BoardGenerator`, `GameController`, `Player` | **uses** (entry point) |

## Design Patterns Used

- **Factory Pattern** - `BoardGenerator` creates `Board` instances with randomly generated snakes and ladders based on difficulty
- **Single Responsibility Principle** - Each class has one job: `Board` manages the grid, `Dice` throws, `GameController` runs game logic, `Player` tracks state
- **Encapsulation** - Snake/Ladder positions are mapped internally in Board; external code only calls `resolveMove()`

## Game Flow

1. User inputs board size, number of players, difficulty, and player names
2. `BoardGenerator` generates a board with random snakes and ladders
3. `GameController` runs turn-by-turn: throw dice -> move player -> check snake/ladder -> check finish
4. Players who reach the final cell are ranked; last remaining player ends the match

## How to Run

```bash
cd snake-and-ladder/src
javac com/example/snakeladder/*.java
java com.example.snakeladder.App
```

## Sample Interaction

```
Enter board size (n for n x n board): 10
Enter number of players: 2
Enter difficulty level (easy/hard): easy
Enter name for Player 1: Alice
Enter name for Player 2: Bob

--- Game Start ---

Board size: 10x10 (cells 1 to 100)
Snakes: {27=5, 98=12, ...}
Ladders: {3=38, 45=79, ...}

Alice rolled 4: moved from 0 to 4
Bob rolled 6: moved from 0 to 6
...
Alice finished the race! (Rank #1)
Match Complete! Bob is the last player remaining.
```
