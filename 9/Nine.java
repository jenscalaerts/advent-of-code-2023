import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nine {

    public static void main(String[] args) throws IOException {
        List<List<int[]>> reports = Files.readAllLines(Paths.get(args[0]))
                .stream()
                .map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray())
                .map(line -> getReport(line))
                .toList();

        System.out.println("part 1: " + calculatePart1(reports));
        System.out.println("part 2: " + calculatePart2(reports));
    }

    private static int calculatePart1(List<List<int[]>> lines) {
        return lines.stream()
                .mapToInt(Nine::extrapolate)
                .sum();
    }

    private static int calculatePart2(List<List<int[]>> lines) {
        return lines.stream()
                .mapToInt(Nine::extrapolateBackward)
                .sum();
    }

    static int extrapolateBackward(List<int[]> report) {
        int val = 0;
        for (int i = report.size() - 1; i >= 0; i--) {
            val = report.get(i)[0] - val;
            System.out.print(val + ",");
            System.out.println(report.get(i));
        }
        return val;
    }

    static int extrapolate(List<int[]> report) {
        return report.stream()
                .mapToInt(r -> r[r.length - 1])
                .sum();
    }

    private static List<int[]> getReport(int[] row) {
        List<int[]> rows = new ArrayList<>();
        rows.add(row);
        while (!areAllZero(rows.get(rows.size() - 1))) {
            int[] currentRow = rows.get(rows.size() - 1);
            int[] nextRow = new int[currentRow.length - 1];
            for (int i = 0; i < currentRow.length - 1; i++) {
                nextRow[i] = currentRow[i + 1] - currentRow[i];
            }
            rows.add(nextRow);
        }
        return rows;
    }

    static int arraySum(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    static boolean areAllZero(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != -0)
                return false;
        }
        return true;
    }

}
