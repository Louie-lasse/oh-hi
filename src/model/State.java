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

    @Override
    public String toString(){
        return switch (this.ordinal()) {
            case 0 -> "NONE";
            case 1 -> "RED";
            case 2 -> "BLUE";
            default -> "INVALID";
        };
    }
}
