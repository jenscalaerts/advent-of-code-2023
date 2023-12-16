record Coordinate(int row, int column) {
    Coordinate plus(Vector vector) {
        return new Coordinate(row + vector.getRow(), column + vector.getColumn());
    }
}
