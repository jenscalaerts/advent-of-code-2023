import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Four {

    public static void main(String[] args) throws IOException {

        List<String> lines = Files.readAllLines(Path.of(args[0]));
        long sum = getGames(lines)
                .mapToLong(i-> getNumberOfMatches(i))
                .filter(i -> i != 0)
                .map(i -> (long) Math.pow(2, i - 1))
                .sum();
        System.out.println("result 1 = " + sum);
        long[] numberOfCards = new long[lines.size()];
        Arrays.fill(numberOfCards, 1);
        Four.Game[] games = getGames(lines)
                .toArray(Four.Game[]::new);
        for (int i = 0; i < games.length; i++) {
            long matches  = getNumberOfMatches(games[i]);
            for(int j = i + 1; j < (int) Math.min(i+games.length-1, i + matches + 1); j++){
                numberOfCards[j] += numberOfCards[i];
            }
        }
        long result = LongStream.of(numberOfCards)
                .sum();
        System.out.println(Arrays.toString(numberOfCards));
        System.out.println(result);


    }

    private static long getNumberOfMatches(Four.Game i) {
        return Arrays.stream(i.numbers())
                .filter(i.winning()::contains)
                .count();
    }

    private static Stream<Four.Game> getGames(List<String> lines) throws IOException {
        return lines.stream()
                .map(line -> parseGame(line));
    }

    private static Game parseGame(String line){
        String[] split = line.split(":\\s+");
        System.out.println(Arrays.toString(split));
        int game = Integer.parseInt(split[0].split("\\s+")[1]);
        String[] gameParts = split[1].split(" \\|\\s+");
        Set<Integer> collect = extractNumbers(gameParts[0])
                .boxed()
                .collect(Collectors.toSet());
        return new Game(game,collect,
                extractNumbers(gameParts[1]).toArray());
    }

    private static IntStream extractNumbers(String spacedList) {
        String[] split = spacedList.split("\\s+");
        System.out.println(Arrays.toString(split));
        return Stream.of(split)
                .mapToInt(Integer::parseInt);

    }

    record Game(int number, Set<Integer> winning, int[] numbers){

    }
}
