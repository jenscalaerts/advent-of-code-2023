package thirteen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import grid.Grid;

public class Thirteen {
    public static void main(String[] args) throws IOException {
        String[] gridStrings = Files.readString(Path.of(args[0]))
                .split("\n\n");
        long total = Arrays.stream(gridStrings)
                .map(Grid::new)
                .mapToInt(grid -> getScoreOfGrid(grid, list -> findMirror(list)))
                .sum();
        System.out.println(total);
        long smudgedTotal = Arrays.stream(gridStrings)
                .map(Grid::new)
                .mapToInt(grid -> getScoreOfGrid(grid, list -> findSmudgedMirror(list)))
                .sum();
        System.out.println(smudgedTotal);
    }

    private static int getScoreOfGrid(Grid grid, Function<List<List<Character>>, Optional<Integer>> mirrorDetector) {
        return mirrorDetector.apply(grid.getColumns())
                .orElseGet(() -> mirrorDetector.apply(grid.getRows()).orElseThrow() * 100);
    }

    public static Optional<Integer> findMirror(List<List<Character>> rows) {
        for (int i = 1; i < rows.size(); i++) {
            if (isMirrorAt(rows, i)) {
                return (Optional.of(i));
            }
        }
        return Optional.empty();
    }

    public static Optional<Integer> findSmudgedMirror(List<List<Character>> rows) {
        for (int i = 1; i < rows.size(); i++) {
            if (isSmudgedMirrorAt(rows, i)) {
                return (Optional.of(i));
            }
        }
        return Optional.empty();
    }

    public static boolean isMirrorAt(List<List<Character>> rows, int checkingIndex) {
        for (int j = 0; j < Math.min(checkingIndex, rows.size() - checkingIndex); j++) {
            if (!rows.get(checkingIndex - 1 - j).equals(rows.get(checkingIndex + j))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSmudgedMirrorAt(List<List<Character>> rows, int checkingIndex) {
        int diffs = 0;
        for (int rowOffset = 0; rowOffset < Math.min(checkingIndex, rows.size() - checkingIndex); rowOffset++) {
            for (int col = 0; col < rows.get(0).size(); col++) {
                Character up = rows.get(checkingIndex - 1 - rowOffset).get(col);
                Character down = rows.get(checkingIndex + rowOffset).get(col);
                if (!up.equals(down)) {
                    diffs++;
                    //early return
                    if (diffs > 1)
                        return false;
                }
            }

        }
        return diffs == 1;

    }
}
