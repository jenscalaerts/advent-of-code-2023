package twentyone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import grid.Coordinate;
import grid.Direction;
import grid.Grid;

public class TwentyOne {

    public static void main(String[] args) throws IOException {
        String gridContent = Files.readString(Paths.get(args[0]));
        Grid grid = new Grid(gridContent);
        Set<Coordinate> knownLocation = calculateNumberVisited(grid, 64);
        System.out.println("solution one =" + knownLocation.size());
        List<Integer> numberOfSteps = new ArrayList<>();
        int stepSize = grid.getWidth();
        int x = 26501365 % stepSize;
        int a = calculateNumberVisitedUnlimited(grid, x);
        int b = calculateNumberVisitedUnlimited(grid, x + grid.getWidth());
        int c = calculateNumberVisitedUnlimited(grid, x + (2 * grid.getWidth()));
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);

        int fd0 = b - a;
        int fd1 = c - b;
        int sd = fd1 - fd0;
        int A = (sd) / 2;
        int B = fd0 - 3 * A;
        int C = a - B - A;

        int solx = 26501365 / stepSize;
        System.out.println(A);
        System.out.println(B);
        System.out.println(C);
        long ceilDiv = Math.ceilDiv(26501365, stepSize);
        System.out.println(Math.addExact(Math.multiplyExact(A, 2*ceilDiv * ceilDiv), Math.multiplyExact(B, ceilDiv))+C);

    }

    private static int calculateNumberVisitedUnlimited(Grid grid, int numberOfSteps) {
        int stepRem = numberOfSteps % 2;
        Coordinate beginning = grid.findAllOfSymbol('S').getFirst();
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(beginning, numberOfSteps));
        Set<Coordinate> knownLocation = new HashSet<>();
        while (!queue.isEmpty()) {
            Node end = queue.poll();
            List<Coordinate> newLocations = Stream.of(Direction.N, Direction.S,
                    Direction.E, Direction.W)
                    .map(end.coord()::plus)
                    .filter(coor -> grid.getSymbolAt(normalizeCoordinate(grid, coor)) != '#')
                    .filter(t -> !knownLocation.contains(t))
                    .toList();
            if (end.stepsLeft() - 1 > 0)
                newLocations.stream()
                        .map(coord -> new Node(coord, end.stepsLeft() - 1))
                        .forEach(queue::add);
            if (end.stepsLeft() % 2 != stepRem)
                knownLocation.addAll(newLocations);
        }

        return knownLocation.size();
    }

    private static Coordinate normalizeCoordinate(Grid grid, Coordinate coor) {
        return new Coordinate(normalizedIndex(coor.row(), grid.getHeight()),
                normalizedIndex(coor.column(), grid.getWidth()));
    }

    private static int normalizedIndex(int i, int normalizer) {
        int rem = i % normalizer;
        return rem >= 0 ? rem : normalizer + rem;
    }

    private static Set<Coordinate> calculateNumberVisited(Grid grid, int numberOfSteps) {
        Coordinate beginning = grid.findAllOfSymbol('S').getFirst();
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(beginning, 64));
        Set<Coordinate> knownLocation = new HashSet<>();
        knownLocation.add(beginning);
        while (!queue.isEmpty()) {
            Node end = queue.poll();
            List<Coordinate> newLocations = Stream.of(Direction.N, Direction.S,
                    Direction.E, Direction.W)
                    .map(end.coord()::plus)
                    .filter(grid::contains)
                    .filter(coor -> grid.getSymbolAt(coor) != '#')
                    .filter(t -> !knownLocation.contains(t))
                    .toList();
            if (end.stepsLeft() - 1 > 0)
                newLocations.stream()
                        .map(coord -> new Node(coord, end.stepsLeft() - 1))
                        .forEach(queue::add);
            if (end.stepsLeft() % 2 == 0)
                knownLocation.addAll(newLocations);
        }
        return knownLocation;
    }

}

record Node(Coordinate coord, int stepsLeft) {
}
