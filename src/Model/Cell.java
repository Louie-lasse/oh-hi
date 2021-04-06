package Model;

import java.util.Random;

public class Cell implements ICell{
    private boolean locked = false;
    private State state;
    private static final Random random = new Random();

    Cell(State startingState){
        this.state=startingState;
    }

    Cell(){
        this(State.NONE);
    }

    public void nextState(){
        if (locked) return;
        switch (state.ordinal()) {
            case (0) -> setState(State.RED);
            case (1) -> setState(State.BLUE);
            case (2) -> makeEmpty();
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

    public void fillWithRandomColor(){
        if (random.nextBoolean()){
            state = State.BLUE;
        } else {
            state = State.RED;
        }
    }

    public void invertColor(){
        state = state.inverse();
    }

    public Cell copy(){
        return new Cell(state);
    }

    public void lock(){
        locked = true;
    }

    public boolean isLocked(){
        return locked;
    }
}
