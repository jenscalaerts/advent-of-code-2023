package fourteen;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import grid.Grid;
import grid.Coordinate;
import grid.Vector;

public class Fourteen {
    private static final char SQUARED_ROCK = '#';
    public static final char EMPTY = '.';
    public static final char ROUNDED_ROCK = 'O';

    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of(args[0]));
        Grid grid = new Grid(input);
        rollBoulders2(grid, Vector.N);
        int sum = northWeight(grid);
        System.out.println("Weight 1:" + sum);

        int northWeight = calculateWeightAfter1M(grid);
        System.out.println(northWeight);

    }

    private static int calculateWeightAfter1M(Grid grid) {
        List<Grid> history = new ArrayList<>();
        int startRepeat = 0;
        for (int i = 0; i < 1_000_000_000; i++) {
            doARotation(grid);
            if (history.contains(grid)) {
                startRepeat = history.indexOf(grid);
                break;
            }
            history.add(new Grid(grid));
        }
        int indexInRepeat = (1_000_000_000 - 1 - startRepeat) % (history.size() - startRepeat);
        int northWeight = northWeight(history.get(indexInRepeat + startRepeat));
        return northWeight;
    }

    private static void doARotation(Grid grid) {
        for (Vector direction : List.of(Vector.N, Vector.W, Vector.S, Vector.E)) {
            rollBoulders2(grid, direction);
        }
    }

    private static int northWeight(Grid grid) {
        int gridHeight = grid.getHeight();
        return grid.findAllOfSymbol(ROUNDED_ROCK)
                .stream()
                .mapToInt(coord -> gridHeight - coord.row())
                .sum();
    }

    private static void rollBoulders(Grid grid) {
        for (int col = 0; col < grid.getWidth(); col++) {
            int openSpot = 0;
            for (int row = 0; row < grid.getHeight(); row++) {
                Coordinate currentCoordinate = new Coordinate(row, col);
                switch (grid.getSymbolAt(currentCoordinate)) {
                    case ROUNDED_ROCK -> grid.swap(new Coordinate(openSpot++, col), currentCoordinate);
                    case SQUARED_ROCK -> openSpot = row + 1;
                }
            }
        }
    }

    private static void rollBoulders2(Grid grid, Vector direction) {
        Vector searchingDirection = direction.invert();

        Coordinate start = switch (direction) {
            case N, W -> new Coordinate(0, 0);
            case E -> new Coordinate(0, grid.getWidth() - 1);
            case S -> new Coordinate(grid.getHeight() - 1, 0);
        };

        while (grid.contains(start)) {
            Coordinate next = start;
            Coordinate nextEmptySpot = start;
            while (grid.contains(next)) {
                switch (grid.getSymbolAt(next)) {
                    case ROUNDED_ROCK -> {
                        grid.swap(next, nextEmptySpot);
                        nextEmptySpot = nextEmptySpot.plus(searchingDirection);
                    }
                    case SQUARED_ROCK -> nextEmptySpot = next.plus(searchingDirection);
                }
                next = next.plus(searchingDirection);
            }
            start = start.plus(direction.isVertical() ? Vector.E : Vector.S);
        }

    }

}
