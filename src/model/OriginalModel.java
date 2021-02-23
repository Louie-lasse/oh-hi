package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OriginalModel implements IModel{

    private ICell[][] world;

    private int size;

    private boolean completed;

    public OriginalModel() {}

    public void update(){}

    public ICell[][] getWorld(){
        return world;
    }

    private final Random random = new Random();

    public void createWorld(int size){
        this.size = size;
        world = new Cell[size][size];
        fillWorldWithColoredCells();
        removeAllRemovableCells();
    }

    private void fillWorldWithColoredCells(){
        fillWorldWithEmptyCells();
        colorAllCellsInWorld();
    }

    private void fillWorldWithEmptyCells(){
        for (ICell[] row: world){
            for (int col = 0; col < size; col++){
                row[col] = new Cell();
            }
        }
    }

    private void colorAllCellsInWorld() {
        int filledCells = 0;
        boolean lookForProvenCells = false;
        while (filledCells < size * size){
            if (lookForProvenCells) {
                filledCells += fillAllProvenCells();
            } else {
                filledCells += fillRandomCell();
            }
            lookForProvenCells = !lookForProvenCells;
        }
    }


    private int fillRandomCell(){
        //FIXME Is it less random if the position is not random?
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
                if (proof.isColored()){
                    world[row][col].setColor(proof.getColor());
                    row = 0;
                    col = 0;
                    filledCells++;
                }
            }
        }
        return filledCells;
    }

    private void removeAllRemovableCells(){
        List<ICell> provenCells;
        ICell cell;
        while (true){
            provenCells = getAllProvenCells();
            if (provenCells.size() == 0) break;
            cell = getRandom(provenCells);
            cell.makeEmpty();
        }
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

    private Proof getProof(int row, int column){
        Proof proof = new Proof();
        proof.add(provableByNeighbour(row, column));
        //proof.add(provableOnRow(row, column));
        //proof.add(provableOnCol(row, column));
        //proof.add(provableBySameRow(position));
        //proof.add(provableByOddOneOut(position));
        return proof;
    }

    //simplification of provableByNeighbour
    private Proof provableByNeighbour(int row, int column){
        Proof proof = new Proof();
        List<ICell> cellsOnRow = getNeighboursOnRow(row, column);
        List<ICell> cellsOnColumn = getNeighboursOnColumn(row, column);
        proof.add(findNeighbourProof(cellsOnRow));
        proof.add(findNeighbourProof(cellsOnColumn));
        return proof;
    }

    private List<ICell> getNeighboursOnRow(int row, int column){
        List<ICell> cells = new ArrayList<>();
        if (column-2 >= 0){
            cells.add(world[row][column-2]);
        }
        if (column-1 >= 0){
            cells.add(world[row][column-1]);
        }
        if (column+1 < size){
            cells.add(world[row][column+1]);
        }
        if (column+2 < size){
            cells.add(world[row][column+2]);
        }
        return cells;
    }

    private List<ICell> getNeighboursOnColumn(int row, int column){
        List<ICell> cells = new ArrayList<>();
        if (row >= 2){
            cells.add(world[row-2][column]);
        }
        if (row >= 1){
            cells.add(world[row-1][column]);
        }
        if (row+1 < size){
            cells.add(world[row+1][column]);
        }
        if (row+2 < size){
            cells.add(world[row+2][column]);
        }
        return cells;
    }

    private Proof findNeighbourProof(List<ICell> cells){
        Proof proof = new Proof();
        ICell previous = new Cell();
        for (ICell cell: cells){
            if (cell.equals(previous)){
                proof.add(cell.getState().inverse());
            }
            previous = cell;
        }
        return proof;
    }

    //TODO det här ser wack ut
    private Proof provableOnRow(int row, int column){
        Proof proof = new Proof();
        int blueCount = countOnRow(row, State.BLUE);
        int redCount = countOnRow(row, State.RED);
        if (blueCount + 1 >= size/2){
            proof.add(State.BLUE); //är detta verkligen rätt
        }
        if (redCount +1 >= size/2){
            proof.add(State.RED);
        }
        return proof;
    }

    private int countOnRow(int row, State color){
        int count = 0;
        for (ICell cell: world[row]){
            if (cell.getState().equals(color)) count++;
        }
        return count;
    }


    private Proof provableOnCol(int row, int column){
        Proof proof = new Proof();
        int blueCount = countOnColumn(row, State.BLUE);
        int redCount = countOnColumn(row, State.RED);
        if (blueCount + 1 >= size/2){
            proof.add(State.BLUE);
        }
        if (redCount +1 >= size/2){
            proof.add(State.RED);
        }
        return proof;    }

    private Proof countOnColumn(Position position){
        Proof proof = new Proof();
        int blueCount = countOnColumn(position.column, State.BLUE);
        int redCount = countOnColumn(position.column, State.RED);
        if (blueCount + 1 >= size/2){
            proof.add(State.BLUE);
        }
        if (redCount +1 >= size/2){
            proof.add(State.RED);
        }
        return proof;
    }

    private int countOnColumn(int col, State color){
        int count = 0;
        for (ICell[] cell: world){
            if (cell[col].getState().equals(color)) count++;
        }
        return count;
    }

    private Proof provableBySameRow(Position position){return new Proof();}

    private Proof provableByOddOneOut(Position position){return new Proof();}

    private List<ICell> getAllProvenCells(){
        List<ICell> provenCells = new ArrayList<>();
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                if (isProven(row, col) && world[row][col].isFilled()){
                    provenCells.add(world[row][col]);
                }
            }
        }
        return provenCells;
    }

    private boolean isProven(int row, int col){
        return getProof(row, col).isColored();
    }

    private ICell getRandom(List<ICell> cells){
        int randomIndex = random.nextInt( cells.size() );
        return cells.get(randomIndex);
    }


}
