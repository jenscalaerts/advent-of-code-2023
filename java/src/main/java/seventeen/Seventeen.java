package seventeen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import grid.Coordinate;
import grid.Direction;
import grid.Grid;

public class Seventeen {
    public static void main(String[] args) throws IOException {
        Grid grid = new Grid(Files.readString(Path.of(args[0])));
        Coordinate begin = new Coordinate(0, 0);
        List<CauldronPath> horizontals = generateVectors(begin, Direction.E)
                .map(vec -> initializePath(grid, vec))
                .collect(Collectors.toList());
        Map<Coordinate, CauldronPath> horizontalOptimals = horizontals.stream()
                .collect(Collectors.toMap(i -> i.getEnd().getEnd(), i -> i));
        List<CauldronPath> verticals = generateVectors(begin, Direction.S)
                .map(vec -> initializePath(grid, vec))
                .collect(Collectors.toList());
        Map<Coordinate, CauldronPath> verticalOptimals = verticals.stream()
                .collect(Collectors.toMap(i -> i.getEnd().getEnd(), i -> i));
        verticals.addAll(horizontals);
        List<CauldronPath> newPaths = verticals;
        while (!newPaths.isEmpty()) {
            List<CauldronPath> processingPaths = newPaths;
            newPaths = new ArrayList<>();
            for (CauldronPath path : processingPaths) {
                Vector end = path.getEnd();
                if (end.direction().isVertical()) {
                    List<CauldronPath> newOptimals = generateHorizontal(end.getEnd())
                            .filter(vec -> grid.contains(vec.getEnd()))
                            .filter(vec -> !horizontalOptimals.containsKey(vec.getEnd())
                                    || horizontalOptimals.get(vec.getEnd()).getHeathLoss() > path.getHeathLoss()
                                            + calculateHeathLoss(grid, vec))
                            .map(vec -> path.add(vec, calculateHeathLoss(grid, vec)))
                            .toList();
                    newPaths.addAll(newOptimals);
                    for (CauldronPath newOptimal : newOptimals) {
                        horizontalOptimals.put(newOptimal.getEnd().getEnd(), newOptimal);
                    }
                } else {
                    List<CauldronPath> newOptimals = generateVertical(end.getEnd())
                            .filter(vec -> grid.contains(vec.getEnd()))
                            .filter(vec -> !verticalOptimals.containsKey(vec.getEnd())
                                    || verticalOptimals.get(vec.getEnd()).getHeathLoss() > path.getHeathLoss()
                                            + calculateHeathLoss(grid, vec))
                            .map(vec -> path.add(vec, calculateHeathLoss(grid, vec)))
                            .toList();
                    newPaths.addAll(newOptimals);
                    for (CauldronPath newOptimal : newOptimals) {
                        verticalOptimals.put(newOptimal.getEnd().getEnd(), newOptimal);
                    }
                }
            }
        }

        Coordinate bottomRight = new Coordinate(grid.getHeight() - 1, grid.getWidth() - 1);
        System.out.println(Math.min(horizontalOptimals.get(bottomRight).getHeathLoss(),
                verticalOptimals.get(bottomRight).getHeathLoss()));
    }

    private static CauldronPath initializePath(Grid grid, Vector vec) {
        int heathloss = calculateHeathLoss(grid, vec);
        return new CauldronPath(vec, heathloss);
    }

    private static int calculateHeathLoss(Grid grid, Vector vec) {
        return vec.getParts()
                .mapToInt(coord -> Character.getNumericValue(grid.getSymbolAt(coord)))
                .sum();
    }

    public static Stream<Vector> generateHorizontal(Coordinate coord) {
        return Stream.concat(generateVectors(coord, Direction.E),
                generateVectors(coord, Direction.W));
    }

    public static Stream<Vector> generateVertical(Coordinate coord) {
        return Stream.concat(generateVectors(coord, Direction.N),
                generateVectors(coord, Direction.S));
    }

    public static Stream<Vector> generateVectors(Coordinate coordinate, Direction direction) {
        return IntStream.range(1, 4)
                .mapToObj(i -> new Vector(coordinate, direction, i));
    }
}

record Vector(Coordinate location, Direction direction, int length) {
    Coordinate getEnd() {
        return location.plus(direction, length());
    }

    Stream<Coordinate> getParts() {
        Builder<Coordinate> builder = Stream.builder();
        Coordinate coordinate = location();
        for (int i = 0; i < length; i++) {
            coordinate = coordinate.plus(direction());
            builder.add(coordinate);
        }
        return builder.build();
    }
}

class CauldronPath {
    private final List<Vector> path;
    private final int heathLoss;

    CauldronPath(Vector start, int heathLoss) {
        this.path = List.of(start);
        this.heathLoss = heathLoss;
    }

    public int getHeathLoss() {
        return heathLoss;
    }

    private CauldronPath(List<Vector> path, int heathLoss) {
        this.path = path;
        this.heathLoss = heathLoss;
    }

    public Vector getCurrentDirection() {
        return path.get(path.size() - 1);
    }

    public CauldronPath add(Vector nextVector, int heathLoss) {
        List<Vector> newPath = new ArrayList<>(this.path);
        newPath.add(nextVector);
        return new CauldronPath(newPath, this.heathLoss + heathLoss);
    }

    Vector getEnd() {
        return path.get(path.size() - 1);
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
