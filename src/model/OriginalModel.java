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
        world = new ICell[size][size];
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
        int filledCells = 0;
        int row = 0;
        int col = 0;
        while (world[row][col].isFilled()){
            row = random.nextInt(size);
            col = random.nextInt(size);
        }
        if (random.nextBoolean()){
            world[row][col].setRed();
        } else {
            world[row][col].setBlue();
        }
        return 1;
    }

    private int fillAllProvenCells() {
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++) {
                if (world[row][col].isFilled()){
                    continue;
                }
                if (isProvable(row, col)){

                }
            }
        }
        return 1;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void changeCell(Position position){
        cellAt(position).nextState();
        update();
    }

    private ICell cellAt(Position position){
        return world[position.row][position.column];
    }

    private boolean isProvable(Position position){
        if (provableByNeighbour(position)) return true;
        if (provableOnRow(position)) return true;
        if (provableOnCol(position)) return true;
        if (provableByOrder(position)) return true;
        if (provableByOddOneOut(position)) return true;
        return false;
    }

    private boolean isProvable(int row, int col){
        return isProvable(new Position(row, col));
    }

    private boolean provableByNeighbour(Position position){return false;}

    private boolean provableOnRow(Position position){return false;}

    private boolean provableOnCol(Position position){return false;}

    private boolean provableByOrder(Position position){return false;}

    private boolean provableByOddOneOut(Position position){return false;}

    private void removeCell(Position position){}

    private List<Position> getRemovableCellsPosition(){
        Position position = new Position();
        List<Position> removables = new ArrayList<>();
        for (int row = 0; row < size; row++){
            position.row = row;
            for (int col = 0; col < size; col++){
                position.column = col;
                if ( isProvable( position ) ){
                    removables.add( position );
                }
            }
        }
        return removables;
    }
}
