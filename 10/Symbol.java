import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class Symbol {
    private final Vector[] directions;
    private final Coordinate location;
    private final char symbol;

    private Symbol(Coordinate location, char symbol, Vector... directions) {
        this.location = location;
        this.directions = directions;
        this.symbol = symbol;
    }

    static Symbol of(Coordinate coordinate, char symbol) {
        return switch (symbol) {
            case 'S', 'R' -> new Symbol(coordinate, symbol, Vector.N, Vector.E, Vector.S, Vector.W);
            case '|' -> new Symbol(coordinate, symbol, Vector.N, Vector.S);
            case '-' -> new Symbol(coordinate, symbol, Vector.E, Vector.W);
            case 'L' -> new Symbol(coordinate, symbol, Vector.N, Vector.E);
            case 'J' -> new Symbol(coordinate, symbol, Vector.N, Vector.W);
            case '7' -> new Symbol(coordinate, symbol, Vector.W, Vector.S);
            case 'F' -> new Symbol(coordinate, symbol, Vector.E, Vector.S);
            case '.' -> new Symbol(coordinate, symbol);
            default -> throw new IllegalArgumentException("Unknown symbol passed %s".formatted(symbol));
        };
    }

    boolean connectsTo(Coordinate coordinate) {
        return Arrays.stream(directions)
                .map(location::plus)
                .anyMatch(coordinate::equals);
    }

    public List<Coordinate> getConnections() {
        return Arrays.stream(directions)
                .map(location::plus)
                .toList();
    }

    public Coordinate getLocation() {
        return location;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol1 = (Symbol) o;
        return symbol == symbol1.symbol && Objects.equals(location, symbol1.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, symbol);
    }

}
