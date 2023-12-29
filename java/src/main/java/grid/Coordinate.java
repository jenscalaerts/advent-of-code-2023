package grid;
public record Coordinate(int row, int column) {
    public Coordinate plus(Direction vector) {
        return new Coordinate(row + vector.getRow(), column + vector.getColumn());
    }

    public int distanceBetween(Coordinate coordinate){
        return Math.abs(this.row() - coordinate.row()) 
        + Math.abs(this.column() - coordinate.column());
        
    }

	public Coordinate plus(Direction direction, int length) {
        return new Coordinate(row + direction.getRow() * length, column + direction.getColumn() * length);
	}
}
