import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Path {
    private final List<Symbol> symbols;
    private final Grid grid;

    public Path(Symbol start, Symbol second, Grid grid) {
        this.symbols = new ArrayList<>();
        symbols.add(start);
        symbols.add(second);
        this.grid = grid;
    }

    public int length() {
        return symbols.size();
    }

    public Symbol getLast() {
        return symbols.get(symbols.size() - 1);
    }

    public void advance() {
        Optional<Symbol> symbol;
        do {
            symbol = getLast().getConnections()
                    .stream()
                    .map(grid::getSymbolAt)
                    .filter(con -> !symbols.contains(con))
                    .findFirst();
            symbol.ifPresent(symbols::add);
        } while (symbol.isPresent());

    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return symbols.toString();
    }

    public boolean contains(Coordinate coordinate) {
        return symbols.stream()
                .anyMatch(i -> i.getLocation().equals(coordinate));
    }
}
