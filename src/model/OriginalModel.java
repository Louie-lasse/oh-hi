package model;

import Application.Main;

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

    public void createWorld(int size) throws IllegalWorldSizeException {
        if (size%2!=0) throw new IllegalWorldSizeException(size);
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
            //TODO remove
            System.out.println("set "+State.RED+" at "+row+", "+col);
        } else {
            world[row][col].setColor(State.BLUE);
            //TODO remove
            System.out.println("set "+State.BLUE+" at "+row+", "+col);
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
                if (proof.getColor() == State.INVALID){
                    //TODO remove
                    Main.displayWorld(world);
                    proof = getProof(row, col);
                }
                if (proof.isColored()){
                    world[row][col].setColor(proof.getColor());
                    //TODO remove
                    System.out.println("set "+proof.getColor()+" at "+row+", "+col+" (proven)");
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
        proof.add(provableByThreeInARow(row, column));
        proof.add(provableBySameAmountInLine(row, column));
        //proof.add(provableOnRow(row, column));
        //proof.add(provableOnCol(row, column));
        //proof.add(provableBySameRow(position));
        //proof.add(provableByOddOneOut(position));
        return proof;
    }

    //simplification of provableByThreeInARow
    private Proof provableByThreeInARow(int row, int column){
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

    private Proof provableBySameAmountInLine(int row, int column){
        Proof proof = new Proof();
        proof.add( proofByAmountInRow(row, column) );
        proof.add( proofByAmountInColumn(row, column));
        return proof;
    }

    private Proof proofByAmountInRow(int row, int column){
        Proof proof = new Proof();
        ICell[] cellsOnRow = collectOtherCellsOnRow(row, column);
        proof.add(lookForMissingColor(cellsOnRow));
        proof.add(lookForSpecialCase(cellsOnRow));
        return proof;
    }

    private ICell[] collectOtherCellsOnRow(int row, int columnToExclude) {
        ICell[] cellsOnRow = new ICell[size - 1];
        int arrayIndex = 0;
        for (int column = 0; column < size; column++) {
            if (column != columnToExclude) {
                cellsOnRow[arrayIndex++] = (world[row][column]);
            }
        }
        return cellsOnRow;
    }

    private Proof proofByAmountInColumn(int row, int column){
        Proof proof = new Proof();
        ICell[] cellsOnColumn = collectOtherCellsOnColumn(row, column);
        proof.add(lookForMissingColor(cellsOnColumn));
        proof.add(lookForSpecialCase(cellsOnColumn));
        return proof;
    }

    private ICell[] collectOtherCellsOnColumn(int rowToExclude, int column){
        ICell[] cellsOnColumn = new ICell[size-1];
        int arrayIndex = 0;
        for (int row = 0; row < size; row++){
            if (row != rowToExclude){
                cellsOnColumn[arrayIndex++] = (world[row][column]);
            }
        }
        return cellsOnColumn;
    }

    private Proof lookForMissingColor(ICell[] cells){
        ColorCounter counter = new ColorCounter();
        counter.add(cells);
        return counter.getMissingColor(size);
    }

    private Proof lookForSpecialCase(ICell[] cells){
        ColorCounter counter = new ColorCounter();
        counter.add(cells);
        Proof oddOneOut = counter.getOddOneOut(size);

        //TODO complete
        return checkForPossibleThreeInRow(cells, oddOneOut.inverse());
    }

    private Proof checkForPossibleThreeInRow(ICell[] cells, State threeInRowColor){
        if (threeInRowColor == State.INVALID){
            return new Proof();
        }
        //TODO complete
        //Do you need to include the cell you are trying to prove???
        //Will excluding the cell lead to the program """finding""" a possible 3 in row, where the cell is actually in the way
        //In that case:
        //maybe rewrite the getOtherNeighbour to NOT exclude it, and Counter to exclude it
        //OR write a getNeighbour cells which includes it
        return new Proof();
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
