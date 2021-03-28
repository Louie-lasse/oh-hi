package model;



public class PositionIterator {
    private final int size;
    private int row;
    private int col;

    PositionIterator(int size){
        this.size = size;
        row = 0;
        col = 0;
    }

    boolean hasNext(){
        return (row < size || col < size);
    }

    Position getNext(){
        Position position = new Position(row, col);
        increasePosition();
        return position;
    }

    private void increasePosition(){
        row++;
        if (row == size){
            row = 0;
            col++;
        }
    }
}
