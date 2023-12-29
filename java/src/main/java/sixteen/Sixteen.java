package sixteen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import grid.Coordinate;
import grid.Direction;
import grid.Grid;

public class Sixteen {
    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of(args[0]));
        Grid grid = new Grid(input);
        var beginBeam = new Beam(new Vector(new Coordinate(0, -1), Direction.E));
        long sum = calculateEnergization(grid, beginBeam);
        System.out.println(sum);
        long maxEnergization = beginnings(grid)
            .map(i -> new Beam(i))
            .mapToLong(beginning -> calculateEnergization(grid, beginning))
            .max().orElseThrow();
        System.out.println(maxEnergization);
    
    }

    static Stream<Vector> beginnings(Grid grid) {
        Builder<Vector> builder = Stream.builder();
        int bottomEdge = grid.getHeight() + 1;
        for (int i = 0; i < grid.getWidth(); i++) {
            builder.add(new Vector(new Coordinate(-1, i), Direction.S));
            builder.add(new Vector(new Coordinate(bottomEdge, i), Direction.N));
        }

        int width = grid.getWidth();
        for (int i = 0; i < grid.getHeight(); i++) {
            builder.add(new Vector(new Coordinate(i, -1), Direction.E));
            builder.add(new Vector(new Coordinate(i, width), Direction.W));
        }
        return builder.build();

    }

    private static long calculateEnergization(Grid grid, Beam beginBeam) {
        List<Beam> beams = new ArrayList<>();
        beams.add(beginBeam);
        while (!beams.stream().allMatch(Beam::isDone)) {
            for (int i = 0; i < beams.size(); i++) {
                Beam beam = beams.get(i);
                if (beam.isDone()) {
                    continue;
                }
                if (!grid.contains(beam.getEnd().getEnd())) {
                    beam.complete();
                    continue;
                }

                List<Vector> next = calculateNextVector(beam, grid);
                Vector nextVector = next.get(0);
                if (beams.stream().noneMatch(b -> b.contains(nextVector))) {
                    beam.add(nextVector);
                } else {
                    beam.complete();
                }
                if (next.size() == 2 && beams.stream().noneMatch(b -> b.contains(next.get(1))))
                    beams.add(new Beam(next.get(1)));
            }
        }

        long sum = beams.stream()
                .flatMap(Beam::getStream)
                .map(Vector::location)
                .filter(grid::contains)
                .distinct()
                .count();
        return sum;
    }

    public static List<Vector> calculateNextVector(Beam beam, Grid grid) {
        Vector end = beam.getEnd();
        Coordinate next = end.getEnd();
        char nextSymbol = grid.getSymbolAt(next);
        return switch (nextSymbol) {
            case '.' -> List.of(new Vector(next, end.direction()));
            case '|' -> handlePipe(end);
            case '-' -> handleDash(end);
            case '/' -> handleForwardSlash(end);
            case '\\' -> handleBackSlash(end);
            default -> throw new IllegalArgumentException();
        };
    }

    private static List<Vector> handleBackSlash(Vector incoming) {
        Direction direction = incoming.direction();
        return List.of(new Vector(incoming.getEnd(), Direction.of(direction.getColumn(), direction.getRow())));
    }

    private static List<Vector> handleForwardSlash(Vector incoming) {
        Direction direction = incoming.direction();
        return List.of(new Vector(incoming.getEnd(), Direction.of(-direction.getColumn(), -direction.getRow())));
    }

    private static List<Vector> handleDash(Vector incoming) {
        if (incoming.direction().isVertical())
            return List.of(new Vector(incoming.getEnd(), Direction.W), new Vector(incoming.getEnd(), Direction.E));
        return List.of(new Vector(incoming.getEnd(), incoming.direction()));
    }

    public static List<Vector> handlePipe(Vector incoming) {
        if (incoming.direction().isVertical())
            return List.of(new Vector(incoming.getEnd(), incoming.direction()));
        return List.of(new Vector(incoming.getEnd(), Direction.S), new Vector(incoming.getEnd(), Direction.N));
    }

}

record Vector(Coordinate location, Direction direction) {
    Coordinate getEnd() {
        return location.plus(direction);
    }
}

class Beam {
    private List<Vector> path = new ArrayList<>();
    private boolean done;

    Beam(Vector start) {
        path.add(start);
    }

    public void complete() {
        done = true;
    }

    public void add(Vector nextVector) {
        path.add(nextVector);
    }

    boolean isDone() {
        return done;
    }

    Vector getEnd() {
        return path.get(path.size() - 1);
    }

    boolean contains(Vector vector) {
        return path.contains(vector);
    }

    int size() {
        return path.size();
    }

    Stream<Vector> getStream() {
        return path.stream();

    }

    @Override
    public String toString() {
        return "%b %s".formatted(done, path);
    }

}
