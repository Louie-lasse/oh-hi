package model;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OriginalModel implements IModel{

    private ICell[][] world;

    private int size;

    private boolean completed;

    public void update(){}

    private final Random random = new Random();

    public void createWorld(int size){
        this.size = size;
        world = new Cell[size][size];
        int filledCells = 0;
        boolean lookForProvenCells = true;
        while (filledCells < size*size){
            if (lookForProvenCells) {
                filledCells += fillAllProvenCells();
            } else {
                filledCells += fillRandomCell();
            }
            lookForProvenCells = !lookForProvenCells;
        }
    }

    private int fillRandomCell(){
        int row = 0;
        int col = 0;
        while (world[row][col].isFilled()){
            row = random.nextInt(size);
            col = random.nextInt(size);
        }
        if (random.nextBoolean()){
            world[row][col].setColor(State.RED);
        } else {
            world[row][col].setColor(State.BLUE);
        }
        return 1;
    }

    private int fillAllProvenCells() {
        int filledCells = 0;
        Proof proof;
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++) {
                if (world[row][col].isFilled()){
                    continue;
                }
                proof = getProof(row, col);
                world[row][col].setColor(proof.getColor());
                row = 0;
                col = 0;
                filledCells++;
            }
        }
        return filledCells;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void nextState(Position position){
        cellAt(position).nextState();
        update();
    }

    private ICell cellAt(Position position){
        return world[position.row][position.column];
    }

    private Proof getProof(Position position){
        Proof p = new Proof();
        p.add(provableByNeighbour(position));
        p.add(provableOnRow(position));
        p.add(provableOnCol(position));
        p.add(provableBySameRow(position));
        p.add(provableByOddOneOut(position));
        return p;
    }

    private Proof getProof(int row, int col){
        return getProof(new Position(row, col));
    }
    //TODO Vastly simplify
    //getting the 4 cells in a List, and checking if 2 or more of them are the same color
    // (if 2 & 2, add RED and BLUE for INVALID)
    private Proof provableByNeighbour(Position position){
        Proof proof = new Proof();
        if (position.row >= 2){
            proof.add(readNeighbourAbove(position));
        }
        if (position.row > 0 && position.row < size-1){
            proof.add(readOnHeight(position));
        }
        if (position.row < size-2){
            proof.add(readNeighbourBelow(position));
        }
        if (position.column >=2){
            proof.add(readNeighbourLeft(position));
        }
        if (position.column > 0 && position.column < size - 1){
            proof.add(readOnWidth(position));
        }
        if (position.column < size -2){
            proof.add(readNeighbourRight(position));
        }
        return proof;
    }

    State readNeighbourAbove(Position position){
        ICell cellAbove1 = world[position.row-1][position.column];
        ICell cellAbove2 = world[position.row-2][position.column];
        if (cellAbove1.equals(cellAbove2)){
            return cellAbove1.getState();
        }
        return State.NONE;
    }

    State readOnHeight(Position position){
        ICell cellAbove = world[position.row-1][position.column];
        ICell cellBelow = world[position.row+1][position.column];
        if (cellAbove.equals(cellBelow)){
            return cellAbove.getState();
        }
        return State.NONE;
    }

    State readNeighbourBelow(Position position){
        ICell cellBelow1 = world[position.row+1][position.column];
        ICell cellBelow2 = world[position.row+2][position.column];
        if (cellBelow1.equals(cellBelow2)){
            return cellBelow1.getState();
        }
        return State.NONE;
    }

    State readNeighbourLeft(Position position){
        ICell cellLeft1 = world[position.row][position.column-1];
        ICell cellBLeft2 = world[position.row][position.column-2];
        if (cellLeft1.equals(cellBLeft2)){
            return cellLeft1.getState();
        }
        return State.NONE;
    }

    State readOnWidth(Position position){
        ICell cellLeft = world[position.row][position.column-1];
        ICell cellRight = world[position.row][position.column+1];
        if (cellLeft.equals(cellRight)){
            return cellLeft.getState();
        }
        return State.NONE;
    }

    State readNeighbourRight(Position position){
        ICell cellRight1 = world[position.row+1][position.column];
        ICell cellRight2 = world[position.row+2][position.column];
        if (cellRight1.equals(cellRight2)){
            return cellRight1.getState();
        }
        return State.NONE;
    }

    private Proof provableOnRow(Position position){return new Proof();}

    private Proof provableOnCol(Position position){return new Proof();}

    private Proof provableBySameRow(Position position){return new Proof();}

    private Proof provableByOddOneOut(Position position){return new Proof();}

    private void removeCell(Position position){}

}
