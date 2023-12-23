import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import grid.Coordinate;
import grid.Grid;

public class Eleven {
    public static void main(String[] args) throws IOException {

        List<String> rows = Files.readAllLines(Path.of(args[0]));
        int expansionRate = Integer.valueOf(args[1]);
        Grid grid = new Grid(rows);
        List<Coordinate> allOfSymbol = grid.findAllOfSymbol('#');

        List<Integer> emptyRows = getEmptyRows(grid);
        List<Integer> emptyColumns = getEmptyColumns(grid);
        System.out.println("rows" +emptyRows);
        System.out.println("cols" +emptyColumns);
        long totalDistance = 0L;
        for (int i = 0; i < allOfSymbol.size(); i++) {
            for (int j = i; j < allOfSymbol.size(); j++) {
                Coordinate left = allOfSymbol.get(i);
                Coordinate right = allOfSymbol.get(j);
                long rowDistance = getExpandedDist(expansionRate, emptyRows, left.row(), right.row());
                long colDistance = getExpandedDist(expansionRate, emptyColumns, left.column(), right.column());
                long dist = rowDistance + colDistance;
                System.out.println("%s %s %d".formatted(left, right, dist));
                totalDistance +=  dist;
            }
        }
        System.out.println(totalDistance);

    }

    private static long getExpandedDist(int expansionRate, List<Integer> emptyColumns, int left, int right) {
        Long colExpansion = emptyColumns.stream()
                .filter(row -> Math.min(left, right) < row && Math.max(left, right) > row)
                .count();
        return Math.abs(right - left) + ((expansionRate - 1) * colExpansion);
    }

    public static void expandsRows(Grid grid) {
        List<Integer> emptyRows = getEmptyRows(grid);
        for (int i = 0; i < emptyRows.size(); i++) {
            grid.insertRow(emptyRows.get(i) + i, '.');
        }
    }

    private static List<Integer> getEmptyRows(Grid grid) {
        List<List<Character>> gridRows = grid.getRows();
        List<Integer> emptyRows = new ArrayList<>();
        for (int i = 0; i < gridRows.size(); i++) {
            var row = gridRows.get(i);
            if (!row.contains('#')) {
                emptyRows.add(i);
            }
        }
        return emptyRows;
    }

    public static void expandsColumns(Grid grid) {
        List<Integer> emptyColumns = getEmptyColumns(grid);
        for (int i = 0; i < emptyColumns.size(); i++) {
            grid.insertColumns(emptyColumns.get(i) + i, '.');
        }
    }

    private static List<Integer> getEmptyColumns(Grid grid) {
        List<List<Character>> gridColumns = grid.getColumns();
        List<Integer> emptyColumns = new ArrayList<>();
        for (int i = 0; i < gridColumns.size(); i++) {
            var row = gridColumns.get(i);
            if (!row.contains('#')) {
                emptyColumns.add(i);
            }
        }
        return emptyColumns;
    }
}
