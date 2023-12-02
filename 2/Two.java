import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Two {
    private static final Map<String, Integer> EXPECTED_MAXIMUM = Map.of("red", 12, "green", 13, "blue", 14);

    public static void main(String[] args) throws IOException {
        String file =  args[0]; 

        int solution1 = getGames(file)
            .filter(Two::hasValuesLowerThenMax) 
            .collect(Collectors.summingInt(Two.Game::number));
        
        System.out.println(solution1);

        int solution2 = getGames(file)
            .mapToInt(game -> game.maxima().values().stream().reduce(1, Math::multiplyExact))
            .sum();
        System.out.println(solution2);

    }

    private static Stream<Two.Game> getGames(String file) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(file));
        Stream<Two.Game> map = lines.stream()
            .map(line -> parseGame(line));
        return map;
    }

    private static boolean hasValuesLowerThenMax(Game game){
        return EXPECTED_MAXIMUM.entrySet().stream()
            .allMatch(val -> val.getValue() >= game.maxima().getOrDefault(val.getKey(), 0));
    }


    private static Game parseGame(String line){
        String[] split = line.split(": ");
        int game = Integer.parseInt(split[0].split(" ")[1]);
        Map<String, Integer> maximums = Arrays.stream(split[1].split("; "))
            .flatMap(draw -> Arrays.stream(draw.split(", ")))
            .map(description -> PullColor.fromDescription(description))
            .collect(Collectors.groupingBy(Two.PullColor::color, Collectors.reducing(0, PullColor::number, Math::max)));

        return new Game(game, maximums);
    }
    
    record PullColor(String color, int number){
        static PullColor fromDescription(String description){
            String[] split = description.split(" ");
            return new PullColor(split[1], Integer.parseInt(split[0]));
        }
    }

    record Game(int number, Map<String, Integer> maxima){
    }
}
