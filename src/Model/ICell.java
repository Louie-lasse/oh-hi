package Model;

public interface ICell {
    void nextState();
    void makeEmpty();
    void setState(State color);
    State getState();
    boolean isEmpty();
    boolean isFilled();
    void fillWithRandomColor();
    boolean isValid();
    @Override
    boolean equals(Object c);
    void invertColor();
    Cell copy();
}
