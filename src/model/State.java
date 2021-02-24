package model;

public enum State {
    NONE(false),
    RED(true),
    BLUE(true),
    INVALID(false);

    final boolean isColored;

    State(boolean colored){
        isColored = colored;
    }

    public State inverse(){
        if (this==RED) return BLUE;
        if (this==BLUE) return RED;
        return this;
    }
}
