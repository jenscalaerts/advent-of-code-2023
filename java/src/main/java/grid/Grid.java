package grid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Grid {
    private final List<List<Character>> data;

    public Grid(List<String> data) {
        this.data = data.stream()
                .map(s -> s.chars().mapToObj(i -> (char) i).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public Grid(String data) {
        this(List.of(data.split("\n")));
    }

    public Grid(Grid grid){
        data = grid.getRows()
        .stream()
        .map(ArrayList::new)
        .collect(Collectors.toList());

    }

    public char getSymbolAt(Coordinate coordinate) {
        return data.get(coordinate.row()).get(coordinate.column());
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

    public List<List<Character>> getRows() {
        return data;
    }

    public List<List<Character>> getColumns() {
        int nColums = data.get(0).size();
        List<List<Character>> columns = new ArrayList<>(nColums);
        for (int i = 0; i < nColums; i++) {
            columns.add(new ArrayList<>(data.size()));
        }

        for (int row = 0; row < data.size(); row++) {
            for (int col = 0; col < nColums; col++) {
                columns.get(col).add(getSymbolAt(new Coordinate(row, col)));
            }
        }

        return columns;
    }

    public void insertRow(int index, char c) {
        int width = data.get(0).size();
        ArrayList<Character> element = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            element.add(c);
        }
        data.add(index, element);
    }

    public void print() {
        for (int i = 0; i < data.size(); i++) {
            List<Character> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                System.out.print(row.get(j));
            }
            System.out.println();
        }
    }

    public void insertColumns(int i, char c) {
        data.forEach(row -> row.add(i, c));
    }

    public int getHeight() {
        return data.size();
    }

    public int getWidth() {
        return data.get(0).size();

    }

    public void swap(Coordinate from, Coordinate to) {
        char fromChar = getSymbolAt(from);
        char toChar = getSymbolAt(to);
        setSymbolAt(from, toChar);
        setSymbolAt(to, fromChar);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
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
        Grid other = (Grid) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }


}
