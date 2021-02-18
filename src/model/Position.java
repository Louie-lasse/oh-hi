package model;

public class Position {
    int row;

    int column;

    Position(int row, int column){
        this.row = row;
        this.column = column;
    }

    Position(){
        this(0, 0);
    }
}
