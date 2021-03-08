package model;

public interface ICell {
    void nextState();
    void makeEmpty();
    void setState(State color);
    State getState();
    boolean isEmpty();
    boolean isFilled();

    @Override
    boolean equals(Object c);
    boolean isValid();
}
