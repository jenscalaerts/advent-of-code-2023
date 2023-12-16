enum Vector {
    N(-1, 0),
    E(0, 1),
    S(1, 0),
    W(0, -1);

    private final int row;
    private final int column;

    private Vector(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
