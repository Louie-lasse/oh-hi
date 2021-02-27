package model;

import Application.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OriginalModel implements IModel{

    private ICell[][] world;

    private int size;

    public OriginalModel() {}

    public void update(){}

    public ICell[][] getWorld(){
        return world;
    }

    private final Random random = new Random();

    public void createWorld(int size) throws IllegalWorldSizeException, WorldCreationException {
        if (size%2!=0) throw new IllegalWorldSizeException(size);
        this.size = size;
        world = new Cell[size][size];
        fillWorldWithColoredCells();
        removeAllRemovableCells();
    }

    //TODO remove when testing is done
    public void test(){
        world = new ICell[6][6];
        size = 6;
        fillWorldWithEmptyCells();
        ICell[] row = {new Cell(State.RED), new Cell(), new Cell(), new Cell(State.BLUE), new Cell(), new Cell(State.RED)};
        world[0] = row;
        Main.displayWorld(world);
        fillAllProvenCells();
        Main.displayWorld(world);
    }

    private void fillWorldWithColoredCells() throws WorldCreationException {
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

    private void colorAllCellsInWorld() throws WorldCreationException {
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
        //TODO
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

    private int fillAllProvenCells() throws WorldCreationException {
        int filledCells = 0;
        Proof proof;
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++) {
                if (world[row][col].isFilled()){
                    continue;
                }
                proof = getProof(row, col);
                if (!proof.isValid()){
                    throw new WorldCreationException();
                }
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
        return false;
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
        ICell[] cellsOnRow = collectCellsOnRow(row, column);
        ICell[] cellsOnColumn = collectCellsOnColumn(row, column);
        proof.add(lookForMissingColor(cellsOnRow));
        proof.add(lookForMissingColor(cellsOnColumn));
        proof.add(lookForSpecialCase(cellsOnRow));
        proof.add(lookForSpecialCase(cellsOnColumn));
        return proof;
    }

    private ICell[] collectCellsOnRow(int row, int columnToExclude) {
        ICell[] cellsOnRow = new ICell[size];
        for (int column = 0; column < size; column++) {
            if (column != columnToExclude) {
                cellsOnRow[column] = (world[row][column]);
            } else {
                cellsOnRow[column] = new Cell(State.INVALID);
            }
        }
        return cellsOnRow;
    }

    private ICell[] collectCellsOnColumn(int rowToExclude, int column){
        ICell[] cellsOnColumn = new ICell[size];
        for (int row = 0; row < size; row++){
            if (row != rowToExclude){
                cellsOnColumn[row] = world[row][column];
            } else {
                cellsOnColumn[row] = new Cell(State.INVALID);
            }
        }
        return cellsOnColumn;
    }

    private Proof lookForMissingColor(ICell[] cells){
        ColorCounter counter = new ColorCounter();
        counter.add(cells);
        return counter.getMissingColor(size);
    }

    private State lookForSpecialCase(ICell[] cells){
        ColorCounter counter = new ColorCounter();
        counter.add(cells);
        Proof oddOneOut = counter.getOddOneOut(size);
        if (possibleThreeInRow(cells, oddOneOut.inverse())) {
            return oddOneOut.inverse();
        }
        return State.NONE;
    }

    private boolean possibleThreeInRow(ICell[] cells, State colorToLookFor){
        if (colorToLookFor == State.INVALID){
            return false;
        }
        Proof color = new Proof(colorToLookFor);
        int possibleColorsInRow = 0;
        for (ICell cell: cells){
            color.add(cell);
            if (color.isValid()){
                possibleColorsInRow++;
                if (possibleColorsInRow==3){
                    return true;
                }
            } else {
                possibleColorsInRow = 0;
                color = new Proof(colorToLookFor);
            }
        }
        return false;
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
