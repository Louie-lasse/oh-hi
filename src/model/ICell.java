package model;

public interface ICell {
    void nextState();
    void makeEmpty();
    void setColor(State color);
    State getState();
    boolean isFilled();

    @Override
    boolean equals(Object c);
}
