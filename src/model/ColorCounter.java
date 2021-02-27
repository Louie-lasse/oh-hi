package model;

class ColorCounter {
    private int blueAmount = 0;
    private int redAmount = 0;

    void add(State color){
        if (color == State.RED) redAmount++;
        if (color == State.BLUE) blueAmount++;
    }

    void add(ICell cell){
        add(cell.getState());
    }

    void add(ICell[] cells){
        for (ICell cell: cells){
            add(cell);
        }
    }

    Proof getMissingColor(int size){
        Proof proof = new Proof();
        proof.add(lookForMissingRed(size));
        proof.add(lookForMissingBlue(size));
        return proof;
    }

    State lookForMissingRed(int size){
        if (redAmount < size/2 && blueAmount == size/2) {
            return State.RED;
        }
        return State.NONE;
    }

    State lookForMissingBlue(int size){
        if (blueAmount < size/2 && redAmount == size/2) {
            return State.BLUE;
        }
        return State.NONE;
    }

    Proof getOddOneOut(int size){
        Proof oddOneOut = new Proof();
        if (redAmount + 1 == size/2){
            oddOneOut.add(State.RED);
        }
        if (blueAmount + 1 == size/2){
            oddOneOut.add(State.BLUE);
        }
        return oddOneOut;
    }

}
