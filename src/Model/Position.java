package Model;

public class Position {
    public int row;

    public int column;

    public Position(int row, int column){
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return (position.row == row && position.column == column);
    }
}
