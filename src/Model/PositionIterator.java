package Model;



public class PositionIterator {
    private final int size;
    private int row;
    private int col;

    public PositionIterator(int size){
        this.size = size;
        row = 0;
        col = 0;
    }

    public boolean hasNext(){
        return (row < size);
    }

    public Position getNext(){
        Position position = new Position(row, col);
        increasePosition();
        return position;
    }

    public void reset(){
        row = 0;
        col = 0;
    }

    private void increasePosition(){
        col++;
        if (col == size){
            col = 0;
            row++;
        }
    }
}
