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
        try{
            fillAndGetProvenCells(filledCells);
        } catch (WorldCreationException e){
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
        ICell cell;
        PositionIterator positions = new PositionIterator(size);
        while (positions.hasNext()){
            cell = cellAt(positions.getNext());
            if (cell.isEmpty()) emptyCells.add(cell);
        }
        return emptyCells;
    }

    void saveCompleteWorld(){
        completeWorld = new ICell[size][size];
        PositionIterator positions = new PositionIterator(size);
        while (positions.hasNext()){
            save(positions.getNext());
        }
    }

    private void save(Position p){
        completeWorld[p.row][p.column] = cellAt(p).copy();
    }

    void fillAndGetProvenCells(List<ICell> filledCells) throws WorldCreationException{
        Proof proof;
        ICell cell;
        Position position;
        PositionIterator positionIterator = new PositionIterator(size);
        while (positionIterator.hasNext()){
            position = positionIterator.getNext();
            cell = cellAt(position);
            if (cell.isFilled()){
                continue;
            }
            proof = getProof(position);
            if (!proof.isValid()){
                throw new WorldCreationException();
            }
            if (proof.isColored()){
                cell.setState(proof.getColor());
                positionIterator.reset();
                filledCells.add(cell);
            }
        }
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
        ICell[] worldAsArray = new ICell[size*size];
        PositionIterator positions = new PositionIterator(size);
        int index = 0;
        while (positions.hasNext()){
            worldAsArray[index++] = cellAt(positions.getNext());
        }
        return worldAsArray;
    }

    private void removeIfProven(ICell cell, List<ICell> redundantCells){
        if (!removeIfExplicitlyProven(cell, redundantCells)){
            removeIfImplicitlyProven(cell, redundantCells);
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
        PositionIterator positions = new PositionIterator(size);
        Position p;
        while (positions.hasNext()){
            p = positions.getNext();
            if (!getProof(p).isValid()) return true;
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
        PositionIterator positions = new PositionIterator(size);
        Position p;
        while (positions.hasNext()){
            p = positions.getNext();
            if (!cellAt(p).equals(cellInCompleteWorld(p)))
                return false;
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

    private ICell cellInCompleteWorld(Position position){
        return world[position.row][position.column];
    }

    private Proof getProof(int row, int column){
        //TODO remove and refactor by making provableBy[...] accept (Position position)
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
        //TODO not tested. Needs gui and runtime to test
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
        List<Position> emptyCellPositions = getEmptyCellsPosition();
        for (Position position: emptyCellPositions){
            if (isComplexTipAvailableAt(position))
                return position;
        }
        throw new HelpNotAvalibleException();
    }

    private boolean isComplexTipAvailableAt(Position position){
        List<ICell> consecutivelyProvenCells = new ArrayList<>();
        ICell cell = cellAt(position);
        try{
            cell.fillWithRandomColor();
            fillAndGetProvenCells(consecutivelyProvenCells);
            remove(consecutivelyProvenCells);
            cell.invertColor();
            fillAndGetProvenCells(consecutivelyProvenCells);
            remove(consecutivelyProvenCells);
        } catch (WorldCreationException e){
            remove(consecutivelyProvenCells);
            return true;
        }
        return false;
    }

}
