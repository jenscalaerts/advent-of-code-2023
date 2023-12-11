import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class Eight {

    public static void main(String[] args) throws IOException, InterruptedException {
        var lines = Files.readString(Path.of(args[0]));
        String[] split = lines.split("\n\n");
        var instructions = split[0]
                .replaceAll("\n", "");
        var mapFactory = new MapFactory();
        Stream<ParsedLine> parsed = Arrays.stream(split[1].split("\n"))
                .map(ParsedLine::forLine);
        var nodeMap = mapFactory.createMap(parsed);
        System.out.println("Solution 1 " + caclulateSolution1(instructions, nodeMap.get("AAA")));
        System.out.println("Solution 2 " + calculateSolution2(instructions, nodeMap));
    }

	private static long calculateSolution2(String instructions, Map<String, Eight.Node> nodeMap) {
        List<Eight.Node> startNodes = findStartNodes(nodeMap);
		List<Eight.SearchState> descriptions = startNodes.stream()
                .map(node -> findEndings(node, instructions))
                .map(SearchState::new)
                .toList();

        while (true) {
            Comparator<Eight.SearchState> comparingLong = Comparator.comparingLong(SearchState::getLastVal);
            long found = descriptions.stream().min(comparingLong).orElseThrow().findNext();
            if (descriptions.stream().allMatch(i -> i.getLastVal() == found)) {
                return found;
            }

        }
	}

    static class SearchState {
        private final Eight.EndingDescription description;
        private long lastVal;

        SearchState(EndingDescription description) {
            this.description = description;
            lastVal = description.initialOffset();
        }


        Long findNext() {
            lastVal += description.offset();
            return lastVal;
        }

        long getLastVal() {
            return lastVal;
        }
    }

    static EndingDescription findEndings(Node node, String instructions) {
        int passed = 0;
        var currentNode = node;

        Node firstZ = null;
        int firstOffset = 0;
        while (true) {
            long instructionIndex = passed % instructions.length();
            char instruction = instructions.charAt((int) instructionIndex);
            currentNode = currentNode.proceed(instruction);
            passed++;
            if (currentNode.location.endsWith("Z")) {
                if (firstZ != null) {
                    return new EndingDescription(firstOffset, passed - firstOffset);
                }
                firstZ = currentNode;
                firstOffset = passed;
            }
        }

    }

    record EndingDescription(long initialOffset, long offset) {
    }

    private static List<Eight.Node> findStartNodes(Map<String, Node> nodeMap) {
        return nodeMap.values()
                .stream()
                .filter(node -> node.getLocation().endsWith("A"))
                .toList();
    }

    private static int caclulateSolution1(String instructions, Eight.Node currentNode) {
        int numberOfStepsTaken = 0;
        while (!currentNode.getLocation().equals("ZZZ")) {
            int instructionIndex = numberOfStepsTaken++ % instructions.length();
            char instruction = instructions
                    .charAt(instructionIndex);
            currentNode = currentNode.proceed(instruction);
        }
        return numberOfStepsTaken;
    }

    static class MapFactory {
        Map<String, Node> references = new HashMap<>();

        Map<String, Node> createMap(Stream<ParsedLine> entries) {
            entries.forEach(entry -> findLocation(entry.location())
                    .setLeft(findLocation(entry.left()))
                    .setRight(findLocation(entry.right())));
            return references;
        }

        private Eight.Node findLocation(String location) {
            return references.computeIfAbsent(location, loc -> new Node(loc));
        }
    }

    record ParsedLine(String location, String left, String right) {

        private static Pattern pattern = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");
        static ParsedLine forLine(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches())
                throw new RuntimeException("Line did not match [%s]".formatted(line));
            return new ParsedLine(matcher.group(1), matcher.group(2), matcher.group(3));
        }
    }

    static class Node {

        private final String location;
        private Node left;
        private Node right;

        public Node(String location) {
            this.location = location;
        }

        public Node setLeft(Node node) {
            left = node;
            return this;
        }

        public Node setRight(Node right) {
            this.right = right;
            return this;
        }

        public String getLocation() {
            return location;
        }

        public Node proceed(char direction) {
            if (direction == 'L') {
                return left;
            } else
                return right;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((location == null) ? 0 : location.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;
            return this.location.equals(other.location);
        }

    }
}
