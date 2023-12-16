
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class Ten {


    public static void main(String[] args) throws IOException {
        List<String> rows = Files.readAllLines(java.nio.file.Path.of(args[0]));
        Grid grid = new Grid(rows);
        Path path = createPath(grid);
        System.out.println("solution 1: " + (path.length() / 2));
        markRightSideOfPath(grid, path);
        growRSymbols(grid, path);
        int numberOfRs = grid.findAllOfSymbol('R').size();
        if (grid.getEdgeCharacters().contains('R'))
            System.out.println(grid.size() - numberOfRs - path.length());
        else
            System.out.println(numberOfRs);

    }

    private static Path createPath(Grid grid) {
        Symbol start = grid.getSymbolAt(grid.findStart());
        var path = start.getConnections()
                .stream()
                .filter(grid::contains)
                .map(grid::getSymbolAt)
                .filter(s -> s.connectsTo(start.getLocation()))
                .findFirst()
                .map(s -> new Path(start, s, grid))
                .orElseThrow();
        path.advance();
        return path;
    }

    private static void growRSymbols(Grid grid, Path path) {
        List<Symbol> newLocations;
        do {
            newLocations = grid.findAllOfSymbol('R')
                    .stream()
                    .flatMap(coordinate -> Stream.of(coordinate.plus(Vector.N),
                            coordinate.plus(Vector.S),
                            coordinate.plus(Vector.E),
                            coordinate.plus(Vector.W)))
                    .filter(grid::contains)
                    .map(grid::getSymbolAt)
                    .filter(symbol -> !path.contains(symbol.getLocation()) && symbol.getSymbol() != 'R')
                    .toList();
            newLocations
                    .forEach(symbol -> grid.setSymbolAt(symbol.getLocation(), 'R'));


        } while (!newLocations.isEmpty());
    }

    private static void markRightSideOfPath(Grid grid, Path path) {
        for (int i = 0; i < path.getSymbols().size() - 1; i++) {
            Symbol first = path.getSymbols().get(i);
            markRightSideOfSymbol(grid, path, i, first);

        }
    }

    private static void markRightSideOfSymbol(Grid grid, Path path, int i, Symbol first) {
        Symbol symbol = path.getSymbols().get(i + 1);

        Coordinate previousLocation = first.getLocation();
        if (symbol.getSymbol() == '|')
            if (previousLocation.row() > symbol.getLocation().row()) {
                rIfNotInPath(symbol, Vector.W, grid, path);
            } else {
                rIfNotInPath(symbol, Vector.E, grid, path);
            }
        if (symbol.getSymbol() == '-')
            if (previousLocation.column() < symbol.getLocation().column()) {
                rIfNotInPath(symbol, Vector.N, grid, path);
            } else {
                rIfNotInPath(symbol, Vector.S, grid, path);
            }
        if (symbol.getSymbol() == 'F' && previousLocation.row() > symbol.getLocation().row()) {
            rIfNotInPath(symbol, Vector.N, grid, path);
            rIfNotInPath(symbol, Vector.W, grid, path);
        }
        if (symbol.getSymbol() == '7' && previousLocation.column() < symbol.getLocation().column()) {
            rIfNotInPath(symbol, Vector.N, grid, path);
            rIfNotInPath(symbol, Vector.E, grid, path);
        }
        if (symbol.getSymbol() == 'J' && previousLocation.row() < symbol.getLocation().row()) {
            rIfNotInPath(symbol, Vector.S, grid, path);
            rIfNotInPath(symbol, Vector.E, grid, path);
        }
        if (symbol.getSymbol() == 'L' && previousLocation.column() > symbol.getLocation().column()) {
            rIfNotInPath(symbol, Vector.S, grid, path);
            rIfNotInPath(symbol, Vector.W, grid, path);
        }
    }

    private static void rIfNotInPath(Symbol second, Vector n, Grid grid, Path path) {
        Coordinate coordinate = second.getLocation().plus(n);
        if (grid.contains(coordinate) && !path.contains(coordinate)) {
            grid.setSymbolAt(coordinate, 'R');
        }
    }

}

