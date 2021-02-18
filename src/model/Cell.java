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
            case (0) -> setRed();
            case (1) -> setBlue();
            case (3) -> makeNull();
        }
    }


    public void makeNull(){
        state = State.NONE;
    }

    public void setRed() { state = State.RED; }

    public void setBlue() { state = State.BLUE; }

    public State getState(){return
            state;
    }

    public boolean isFilled(){
        return !state.equals(State.NONE);
    }
}
