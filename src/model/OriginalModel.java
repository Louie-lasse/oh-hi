package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static model.State.*;

public class OriginalModel implements IModel{

    private ICell[][] world;

    private ICell[][] completeWorld;

    private int size;

    public OriginalModel() {}

    public void update(){}

    public ICell[][] getWorld(){
        return world;
    }

    private static final Random random = new Random();

    public void createWorld(int size) throws IllegalWorldSizeException, WorldCreationException {
        if (size%2!=0) throw new IllegalWorldSizeException(size);
        this.size = size;
        world = new Cell[size][size];
        fillWorldWithColoredCells();
        saveCompleteWorld();
        removeAllRemovableCells();
    }

    private void fillWorldWithColoredCells() throws WorldCreationException {
        fillWorldWithEmptyCells();
        addLayer();
    }

    private void fillWorldWithEmptyCells(){
        for (ICell[] row: world){
            for (int col = 0; col < size; col++){
                row[col] = new Cell();
            }
        }
    }

    void addLayer() throws WorldCreationException{
        List<ICell> filledCells = new ArrayList<>();
        if (!fillAndGetProvenCells(filledCells)){
            remove(filledCells);
            throw new WorldCreationException();
        }
        if (!worldIsFilled()){
            ICell randomCell = getRandomEmptyCellFromWorld();
            randomCell.fillWithRandomColor();
            try{
                addLayer();
            } catch (WorldCreationException e1){
                randomCell.invertColor();
                try {
                    addLayer();
                } catch (WorldCreationException e2){
                    remove(filledCells);
                    remove(randomCell);
                    throw new WorldCreationException();
                }
            }
        }
    }

    void remove(ICell cell){
        cell.makeEmpty();
    }

    void remove(List<ICell> cells){
        for (ICell cell: cells){
            remove(cell);
        }
    }

    private boolean worldIsFilled(){
        for (ICell[] row: world){
            for (ICell cell: row){
                if (cell.isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

    private ICell getRandomEmptyCellFromWorld(){
        int randomRow;
        int randomColumn;
        do{
            randomRow = random.nextInt(size);
            randomColumn = random.nextInt(size);
        } while (cellAt(randomRow, randomColumn).isFilled());
        return cellAt(randomRow, randomColumn);
    }

    void saveCompleteWorld(){
        completeWorld = new ICell[size][size];
        for (int row = 0; row < size; row++){
            saveRowInWorld(row);
        }
    }

    private void saveRowInWorld(int row){
        System.arraycopy(world[row], 0, completeWorld[row], 0, size);
    }

    private int fillAllProvenCells() throws WorldCreationException {
        //FIXME don't remove yet. May want to use this to improve removeAllRemovableCells()
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
                    cellAt(row, col).setState(proof.getColor());
                    row = 0;
                    col = 0;
                    filledCells++;
                }
            }
        }
        return filledCells;
    }

    boolean fillAndGetProvenCells(List<ICell> filledCells){
        Proof proof;
        ICell cell;
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++) {
                cell = world[row][col];
                if (cell.isFilled()){
                    continue;
                }
                proof = getProof(row, col);
                if (!proof.isValid()){
                    return false;
                }
                if (proof.isColored()){
                    cell.setState(proof.getColor());
                    row = 0;
                    col = -1;
                    filledCells.add(cell);
                }
            }
        }
        return true;
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

    private ICell cellAt(int row, int column){
        Position position = new Position(row, column);
        return cellAt(position);
    }

    private Proof getProof(int row, int column){
        Proof proof = new Proof();
        proof.add(provableByThreeInARow(row, column));
        proof.add(provableBySameAmountInLine(row, column));
        proof.add(provableByNoSamePattern(row, column));
        return proof;
    }

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
        return NONE;
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

    private Proof provableByNoSamePattern(int row, int column){
        Proof proof = new Proof();
        proof.add(findSimilarRow(row, column));
        proof.add(findSimilarColumn(row, column));
        return proof;
    }

    private Proof findSimilarRow(int row, int column){
        Proof proof = new Proof();
        ICell[] cellsOnRow = collectCellsOnRow(row, column);
        ICell[] alt;
        for (int i = 0; i < size; i++){
            if (i != row){
                alt = collectCellsOnRow(i, column);
                proof.add(findSimilarity(cellsOnRow, alt));
            }
        }
        return proof;
    }

    private Proof findSimilarColumn(int row, int column) {
        Proof proof = new Proof();
        ICell[] cellsOnColumn = collectCellsOnColumn(row, column);
        ICell[] alt;
        for (int i = 0; i < size; i++){
            if (i != column){
                alt = collectCellsOnColumn(row, i);
                proof.add(findSimilarity(cellsOnColumn, alt));
            }
        }
        return proof;
    }

    private State findSimilarity(ICell[] mainCellArray, ICell[] comparedTo){
        ICell colorA = new Cell();
        ICell colorB = new Cell();
        for (int index = 0; index < size; index++){
            if (!mainCellArray[index].isValid()){
                colorA = comparedTo[index];
            } else if (comparedTo[index].isEmpty()) {
                return NONE;
            } else if (mainCellArray[index].isEmpty()){
                if (colorB.isEmpty()){
                    colorB = comparedTo[index];
                } else if (!colorB.equals(comparedTo[index])){
                    return NONE;
                }
            } else {
                if (!mainCellArray[index].equals(comparedTo[index])){
                    return NONE;
                }
            }
        }
        if (colorA == colorB){
            return NONE;
        }
        return colorB.getState();
    }

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
