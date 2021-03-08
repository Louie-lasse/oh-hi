package model;

public class Cell implements ICell{
    private State state;

    Cell(State startingState){
        this.state=startingState;
    }

    Cell(){
        this(State.NONE);
    }

    public void nextState(){
        switch (state.ordinal()) {
            case (0) -> setState(State.RED);
            case (1) -> setState(State.BLUE);
            case (3) -> makeEmpty();
        }
    }

    @Override
    public boolean equals(Object c){
        if (c == this) return true;
        if (!(c instanceof Cell)) return false;
        Cell other = (Cell) c;
        return other.getState().equals(this.getState());
    }

    public void makeEmpty(){
        state = State.NONE;
    }

    public void setState(State color) {
        if (color.isColored) {
            this.state = color;
        }
    }

    public State getState(){
        return state;
    }

    public boolean isEmpty() {
        return state.equals(State.NONE);
    }

    public boolean isFilled(){
        return !isEmpty();
    }

    @Override
    public String toString(){
        return state.toString();
    }

    public boolean isValid(){
        return !(state==State.INVALID);
    }
}
