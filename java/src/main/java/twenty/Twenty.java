package twenty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Twenty {
    public static void main(String[] args) throws IOException {
        List<String> allLines = Files.readAllLines(Path.of(args[0]));
        List<Input> list = allLines.stream()
                .map(line -> Input.create(line))
                .toList();
        Map<String, List<String>> inputs = list.stream()
                .flatMap(input -> Arrays.stream(input.targets()).map(i -> new String[] { input.name(), i }))
                .collect(Collectors.groupingBy(ar -> ar[1], Collectors.mapping(ar -> ar[0], Collectors.toList())));

        solve1(Integer.parseInt(args[1]), getModules(list, inputs));
        solve2(getModules(list, inputs), inputs.get("cs"));
    }

    private static void solve1(int numberOfIterations, Map<String, Module> modules) {
        int lowPulses = 0;
        int highPulses = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            Queue<Pulse> queue = new LinkedList<Pulse>();
            queue.add(new Pulse("", "broadcaster", false));
            do {
                Pulse pulse = queue.poll();
                if (pulse.level())
                    highPulses++;
                else
                    lowPulses++;
                Module module = modules.get(pulse.target());
                if (module != null)
                    module.handle(pulse).forEach(queue::add);
            } while (!queue.isEmpty());
        }
        System.out.println(Math.multiplyFull(lowPulses, highPulses));
    }

    private static void solve2(Map<String, Module> modules, List<String> list) {
        HashMap<String, List<Integer>> collection = new HashMap<>();
        list.forEach(i -> collection.put(i, new ArrayList<>()));
        long solution = findLowPulse(modules, collection);

        System.out.println("2: " + solution);
    }

    private static long findLowPulse(Map<String, Module> modules, HashMap<String, List<Integer>> inputs) {
        int i = 0;
        while(!inputs.values().stream().allMatch(list->list.size() > 0)){
            Queue<Pulse> queue = new LinkedList<Pulse>();
            queue.add(new Pulse("", "broadcaster", false));
            i++;
            do {
                Pulse pulse = queue.poll();

                if (pulse.target().equals("cs") && pulse.level()){
                    inputs.get(pulse.source()).add(i);
                }
                Module module = modules.get(pulse.target());
                if (module != null)
                    module.handle(pulse).forEach(queue::add);
            } while (!queue.isEmpty());
            System.out.println(inputs);
        }
        return inputs.values().stream().map(list->(long)list.get(0)).reduce((l,r) -> lcm(l,r)).orElseThrow();
    }

    public static long lcm(long left, long right) {
        return Math.multiplyExact(Math.divideExact(left, gcd(left, right)), right);
    }

    private static long gcd(long left, long right) {
        while (right != 0) {
            long t = right;
            right = left % right;
            left = t;
        }
        return left;
    }

    private static Map<String, Module> getModules(List<Input> list, Map<String, List<String>> inputs) {
        Map<String, Module> modules = list.stream()
                .map(input -> new Module(input.name(), List.of(input.targets()),
                        createStrategy(input.type(), inputs.get(input.name()))))
                .collect(Collectors.toMap(Module::name, Function.identity()));
        return modules;
    }

    public static EmissionLevelStrategy createStrategy(char type, List<String> inputs) {
        return switch (type) {
            case 'b' -> pulse -> Optional.of(pulse.level());
            case '%' -> new FlipFlop();
            case '&' -> new Conjunction(inputs);
            default -> throw new IllegalArgumentException();
        };

    }

}

record Pulse(String source, String target, boolean level) {

    @Override
    public String toString() {
        return "%s -%s-> %s".formatted(source(), level() ? "high" : "low", target());
    }

}

record Input(String name, String[] targets, char type) {

    public static Input create(String line) {
        String[] parts = line.split(" -> ");
        String[] targets = parts[1].split(", ");
        if (line.charAt(0) != '%' && line.charAt(0) != '&')
            return new Input(parts[0], targets, 'b');
        return new Input(parts[0].substring(1), targets, parts[0].charAt(0));
    }
}

class Module {
    private final String name;
    private final List<String> targets;
    private final EmissionLevelStrategy emissionLevelStrategy;

    public Module(String name, List<String> targets, EmissionLevelStrategy emissionLevelStrategy) {
        this.name = name;
        this.targets = targets;
        this.emissionLevelStrategy = emissionLevelStrategy;
    }

    public Stream<Pulse> handle(Pulse pulse) {
        return emissionLevelStrategy.on(pulse)
                .map(this::createPulses)
                .orElse(Stream.empty());
    }

    private Stream<Pulse> createPulses(boolean level) {
        return targets.stream()
                .map(target -> new Pulse(name, target, level));
    }

    public String name() {
        return name;
    }
}

interface EmissionLevelStrategy {
    Optional<Boolean> on(Pulse pulse);
}

class FlipFlop implements EmissionLevelStrategy {
    private boolean level;

    @Override
    public Optional<Boolean> on(Pulse pulse) {
        boolean level = pulse.level();
        if (level) {
            return Optional.empty();
        }
        this.level = !this.level;
        return Optional.of(this.level);
    }

}

class Conjunction implements EmissionLevelStrategy {
    private Map<String, Boolean> inputs;

    public Conjunction(List<String> inputs) {
        this.inputs = inputs.stream()
                .collect(Collectors.toMap(Function.identity(), ign -> false));
    }

    public void addInputs(List<String> inputs) {
        inputs.stream()
                .collect(Collectors.toMap(Function.identity(), i -> false));
    }

    @Override
    public Optional<Boolean> on(Pulse pulse) {
        inputs.put(pulse.source(), pulse.level());
        Boolean allHigh = inputs.values().stream().reduce(true, (l, r) -> l && r);
        return Optional.of(!allHigh);
    }
}
