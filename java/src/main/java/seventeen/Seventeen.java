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
        new OptimalRouteCalculator(grid, 1, 3).calculate();
        new OptimalRouteCalculator(grid, 4, 10).calculate();
    }

}

class OptimalRouteCalculator {
    private final Grid grid;
    private final int minDistance;
    private final int maxDistance;
    private Map<Coordinate, CauldronPath> horizontalOptimals;
    private Map<Coordinate, CauldronPath> verticalOptimals;

    public OptimalRouteCalculator(Grid grid, int minDistance, int maxDistance) {
        this.grid = grid;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        Coordinate begin = new Coordinate(0, 0);
        horizontalOptimals = generateVectors(begin, Direction.E)
                .map(vec -> initializePath(grid, vec))
                .collect(Collectors.toMap(i -> i.getEnd().getEnd(), i -> i));
        verticalOptimals = generateVectors(begin, Direction.S)
                .map(vec -> initializePath(grid, vec))
                .collect(Collectors.toMap(i -> i.getEnd().getEnd(), i -> i));
    }

    public void calculate() {

        List<CauldronPath> newPaths = new ArrayList<>();
        newPaths.addAll(horizontalOptimals.values());
        newPaths.addAll(verticalOptimals.values());

        while (!newPaths.isEmpty()) {
            List<CauldronPath> processingPaths = newPaths;
            newPaths = new ArrayList<>();
            for (CauldronPath path : processingPaths) {
                Vector end = path.getEnd();
                if (end.direction().isVertical()) {
                    extendVertical(newPaths, path, end);
                } else {
                    extendHorizontal(newPaths, path, end);
                }
            }
        }
        Coordinate bottomRight = new Coordinate(grid.getHeight() - 1, grid.getWidth() - 1);
        System.out.println(Math.min(horizontalOptimals.get(bottomRight).getHeathLoss(),
                verticalOptimals.get(bottomRight).getHeathLoss()));
    }

    private void extendHorizontal(List<CauldronPath> newPaths, CauldronPath path, Vector end) {
        List<CauldronPath> newOptimals = getNewOptimals(verticalOptimals, path,
                generateVertical(end.getEnd()));
        newPaths.addAll(newOptimals);
        for (CauldronPath newOptimal : newOptimals) {
            verticalOptimals.put(newOptimal.getEnd().getEnd(), newOptimal);
        }
    }

    private void extendVertical(List<CauldronPath> newPaths, CauldronPath path, Vector end) {
        List<CauldronPath> newOptimals = getNewOptimals(horizontalOptimals, path,
                generateHorizontal(end.getEnd()));
        newPaths.addAll(newOptimals);
        for (CauldronPath newOptimal : newOptimals) {
            horizontalOptimals.put(newOptimal.getEnd().getEnd(), newOptimal);
        }
    }

    private List<CauldronPath> getNewOptimals(Map<Coordinate, CauldronPath> horizontalOptimals, CauldronPath path,
            Stream<Vector> horizontal) {
        List<CauldronPath> newOptimals = horizontal
                .filter(vec -> grid.contains(vec.getEnd()))
                .filter(vec -> !horizontalOptimals.containsKey(vec.getEnd())
                        || horizontalOptimals.get(vec.getEnd()).getHeathLoss() > path.getHeathLoss()
                                + calculateHeathLoss(grid, vec))
                .map(vec -> path.add(vec, calculateHeathLoss(grid, vec)))
                .toList();
        return newOptimals;
    }

    private CauldronPath initializePath(Grid grid, Vector vec) {
        int heathloss = calculateHeathLoss(grid, vec);
        return new CauldronPath(vec, heathloss);
    }

    private int calculateHeathLoss(Grid grid, Vector vec) {
        return vec.getParts()
                .mapToInt(coord -> Character.getNumericValue(grid.getSymbolAt(coord)))
                .sum();
    }

    public Stream<Vector> generateHorizontal(Coordinate coord) {
        return Stream.concat(generateVectors(coord, Direction.E),
                generateVectors(coord, Direction.W));
    }

    public Stream<Vector> generateVertical(Coordinate coord) {
        return Stream.concat(generateVectors(coord, Direction.N),
                generateVectors(coord, Direction.S));
    }

    public Stream<Vector> generateVectors(Coordinate coordinate, Direction direction) {
        return IntStream.range(minDistance, maxDistance + 1)
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
