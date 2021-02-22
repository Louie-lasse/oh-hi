package model;

public class Proof {
    private State color;

    Proof(){ this.color = State.NONE; }

    void add(State state){
        if (color.equals(State.NONE)){
            color = state;
        }
        if (!state.equals(State.NONE) && !color.equals(state)){
            color = State.INVALID;
        }
    }

    void add(Proof proof){
        this.add(proof.getColor());
    }

    State getColor(){
        return color;
    }

    boolean isColored(){
        return color.isColored;
    }
}
