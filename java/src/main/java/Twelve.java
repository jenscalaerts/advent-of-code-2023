import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Twelve {
    private static Map<Row, Long> knownValues;
    public static void main(String[] args) throws IOException {
        long sum = Files.readAllLines(Path.of(args[0]))
                .stream()
                .map(Twelve::createRow)
                .peek(System.out::println)
                .mapToLong(row -> findNumberOfCombintions(row, new HashMap<>()))
                .peek(System.out::println)
                .sum();

        System.out.println("total" + sum);
        long sum2 = Files.readAllLines(Path.of(args[0]))
                .stream()
                .map(Twelve::createRow2)
                .mapToLong(row -> findNumberOfCombintions(row, new HashMap<>()))
                .peek(System.out::println)
                .sum();
        System.out.println("total" + sum2);
    }

    private static Row createRow(String row) {
        String[] split = row.split(" ");
        List<Integer> groups = Arrays.stream(split[1].split(","))
                .map(Integer::parseInt).toList();
        return new Row(split[0], groups);
    }

    private static Row createRow2(String row) {
        String[] split = row.split(" ");
        List<Integer> groups = Arrays.stream(split[1].split(","))
                .map(Integer::parseInt).toList();
        List<Integer> groupBuilder = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            groupBuilder.addAll(groups);
            
        }
        String[] strings = new String[5];
        Arrays.fill(strings, split[0]);
        String pattern = String.join("?", strings);
        return new Row(pattern, groupBuilder);
    }

    private static long findNumberOfCombintions(Row row, HashMap<Row, Long> cache) {
        if(cache.containsKey(row))
            return cache.get(row);
        Integer groupSize = row.groups().get(0);
        if (row.groups().size() == 1) {
            long count = getMatches(row.pattern(), groupSize)
                    .stream()
                    .map(index -> row.pattern().substring(index + groupSize))
                    .filter(pattern -> !pattern.contains("#"))
                    .count();
            cache.put(row, count);
            return count;
        }

        long sum = getMatches(row.pattern(), row.groups().get(0))
                .stream()
                .map(row::moveToIndex)
                .mapToLong(r -> findNumberOfCombintions(r, cache))
                .sum();
        cache.put(row, sum);
        return sum;
    }

    private static List<Integer> getMatches(String pattern, int groupSize) {
        if (groupSize > pattern.length())
            return List.of();
        List<Integer> matches = new ArrayList<>();
        int firstHash = pattern.indexOf('#');
        int maximumToFit = pattern.length() - groupSize;
        int lastPossible = firstHash != -1 ? Math.min(firstHash, maximumToFit) : maximumToFit;
        for (int i = 0; i <= lastPossible; i++) {
            int endIndex = i + groupSize;
            String window = pattern.substring(i, endIndex);
            if (!window.contains(".") &&
                (pattern.length() == endIndex || pattern.charAt(endIndex) != '#')) {
                matches.add(i);
            }
        }
        return matches;
    }

}

record Row(String pattern, List<Integer> groups) {
    Row moveToIndex(int index) {
        Integer groupSize = groups().get(0);
        int min = Math.min(groupSize + index + 1, pattern.length());
        return new Row(pattern.substring(min), groups.subList(1, groups().size()));
    }

}

record GroupMatches(int groupSize, List<Integer> matches) {
}
