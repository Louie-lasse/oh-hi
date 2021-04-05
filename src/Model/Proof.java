package Model;

public class Proof {
    private State color;

    Proof(State initialColor){
        color = initialColor;
    }

    Proof(){
        this(State.NONE);
    }

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

    void add(ICell cell) {
        this.add(cell.getState());
    }

    State getColor(){
        return color;
    }

    boolean isColored(){
        return color.isColored;
    }

    State inverse(){
        return color.inverse();
    }

    boolean isValid(){
        return color!=State.INVALID;
    }

    @Override
    public String toString(){
        return color.toString();
    }
}
