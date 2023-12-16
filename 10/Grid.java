import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Grid {
    private static final char START_SYMBOL = 'S';
    private final List<List<Character>> data;

    public Grid(List<String> data) {
        this.data = data.stream()
                .map(s -> s.chars().mapToObj(i -> (char) i).collect(Collectors.toList()))
                .toList();
    }

    public Symbol getSymbolAt(Coordinate coordinate) {
        return Symbol.of(coordinate, data.get(coordinate.row()).get(coordinate.column()));

    }

    public Coordinate findStart() {
        return findAllOfSymbol(START_SYMBOL).stream().findFirst().orElseThrow();
    }

    public List<Coordinate> findAllOfSymbol(Character symbol) {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<Character> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == symbol)
                    coordinates.add(new Coordinate(i, j));
            }


        }
        return coordinates;
    }

    public boolean contains(Coordinate coordinate) {
        return coordinate.row() >= 0 && coordinate.column() >= 0
                && coordinate.row() < data.size() && coordinate.column() < data.get(0).size();
    }

    public void setSymbolAt(Coordinate right, Character r) {
        data.get(right.row()).set(right.column(), r);
    }

    public int size() {
        return data.size() * data.get(0).size();
    }

    public Set<Character> getEdgeCharacters() {
        Set<Character> edges = new HashSet<>();
        edges.addAll(data.get(0));
        edges.addAll(data.get(data.size() - 1));
        for (List<Character> row : data) {
            edges.add(row.get(0));
            edges.add(row.get(row.size() - 1));
        }
        return edges;
    }
}
