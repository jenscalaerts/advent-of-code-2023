package grid;
record Coordinate(int row, int column) {
    Coordinate plus(Vector vector) {
        return new Coordinate(row + vector.getRow(), column + vector.getColumn());
    }

    public int distanceBetween(Coordinate coordinate){
        return Math.abs(this.row() - coordinate.row()) 
        + Math.abs(this.column() - coordinate.column());
        
    }
}