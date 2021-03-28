package model;

import java.util.*;

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

    private static Difficulty difficulty = Difficulty.NORMAL;

    public void setDifficulty(Difficulty d){
        difficulty = d;
    }

    public void createWorld(int size) throws IllegalWorldSizeException, WorldCreationException {
        if (size%2!=0) throw new IllegalWorldSizeException(size);
        this.size = size;
        world = new Cell[size][size];
        fillWorldWithColoredCells();
        saveCompleteWorld();
        removeRedundantCells();
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
        List<ICell> emptyCells = getEmptyCellsFromWorld();
        return ModelHelper.randomCell(emptyCells);
    }

    private List<ICell> getEmptyCellsFromWorld(){
        List<ICell> emptyCells = new ArrayList<>();
        for (ICell[] row: world){
            for (ICell cell: row){
                if (cell.isEmpty()){
                    emptyCells.add(cell);
                }
            }
        }
        return emptyCells;
    }

    void saveCompleteWorld(){
        completeWorld = new ICell[size][size];
        //TODO replace with PositionIterator?

        for (int row = 0; row < size; row++){
            saveRowInWorld(row);
        }
    }

    private void saveRowInWorld(int row){
        for (int col = 0; col < size; col++) {
            completeWorld[row][col] = cellAt(row, col).copy();
        }
    }

    boolean fillAndGetProvenCells(List<ICell> filledCells){
        Proof proof;
        ICell cell;
        //TODO please try to replace with PositionIterator
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

    private void removeRedundantCells(){
        ICell[] cellsToCheck = getWorldAsArray();
        ModelHelper.randomize(cellsToCheck);
        List<ICell> redundantCells = new ArrayList<>();
        for (ICell cell: cellsToCheck){
            removeIfProven(cell, redundantCells);
        }
    }

    private ICell[] getWorldAsArray(){
        //TODO replace with PositionIterator
        ICell[] worldAsArray = new ICell[size*size];
        int index = 0;
        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++){
                worldAsArray[index++] = cellAt(row, column);
            }
        }
        return worldAsArray;
    }

    private void removeIfProven(ICell cell, List<ICell> redundantCells){
        if (!removeIfExplicitlyProven(cell, redundantCells)){
            if (difficulty == Difficulty.HARD){
                removeIfImplicitlyProven(cell, redundantCells);
            }
        }
    }

    private boolean removeIfExplicitlyProven(ICell cell, List<ICell> redundantCells){
        cell.invertColor();
        if (anyCellIsInvalid()) {    //if cell is explicitly needed
            remove(cell);
            redundantCells.add(cell);
            return true;
        }
        return false;
    }

    private boolean anyCellIsInvalid(){
        //TODO replace with PositionIterator
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                if (!getProof(row, col).isValid()) return true;
            }
        }
        return false;
    }

    private void removeIfImplicitlyProven(ICell cell, List<ICell> redundantCells){
        cell.invertColor();
        try{
            addLayer();         //fails if the cell is redundant
            cell.invertColor();
            remove(redundantCells);
        } catch (WorldCreationException e){
            redundantCells.add(cell);
            remove(redundantCells);
        }
    }


    public boolean isCompleted(){
        //TODO replace with PositionIterator
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                if (!cellAt(row, col).equals(completeWorld[row][col]))
                    return false;
            }
        }
        return true;
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

    private Proof getProof(Position p){
        return getProof(p.row, p.column);
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

    public Position help() throws HelpNotAvalibleException{
        try{
            return anyCellIsWrong();
        } catch (HelpNotAvalibleException helpNotAvalibleException){
            return findTip();
        }
    }

    private Position anyCellIsWrong() throws HelpNotAvalibleException{
        PositionIterator worldIterator = new PositionIterator(size);
        Position position;
        while (worldIterator.hasNext()){
            position = worldIterator.getNext();
            if (isIncorrect(position))
                return position;
        }
        throw new HelpNotAvalibleException();
    }

    private boolean isIncorrect(Position position){
        if (cellAt(position).isEmpty()) return false;
        return cellAt(position).equals(completeWorld[position.row][position.column]);
    }

    private Position findTip() throws HelpNotAvalibleException{
        try{
            return findSimpleTip();
        } catch (HelpNotAvalibleException helpNotAvalibleException){
            return findComplexTip();
        }
    }

    private Position findSimpleTip() throws HelpNotAvalibleException{
        List<Position> emptyCellsPosition = getEmptyCellsPosition();
        ModelHelper.randomize(emptyCellsPosition);
        for (Position position: emptyCellsPosition){
            if (isProven(position)) return position;
        }
        throw new HelpNotAvalibleException();
    }

    private List<Position> getEmptyCellsPosition(){
        List<Position> positions = new ArrayList<>();
        Position position;
        PositionIterator iterator = new PositionIterator(size);
        while (iterator.hasNext()){
            position = iterator.getNext();
            if (cellAt(position).isEmpty())
                positions.add(position);
        }
        return positions;
    }

    private boolean isProven(Position p){
        return getProof(p).isColored();
    }

    private Position findComplexTip() throws HelpNotAvalibleException{
        //TODO write this. Find all empty cells and iterate the following:
        //if randomColor then fillAllProven crashes for at least one color, it is proven by simple proof
        //can't crash for both, as that would imply an incorrect cell, caught by anyCellIsWrong()
        throw new HelpNotAvalibleException();
    }

}
