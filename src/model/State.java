package model;

public enum State {
    NONE(false),
    RED(true),
    BLUE(true),
    INVALID(false);

    final boolean isColored;

    private State(boolean colored){
        isColored = colored;
    }
}
