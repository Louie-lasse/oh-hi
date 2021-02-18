package model;

public interface ICell {
    void nextState();
    void makeNull();
    void setRed();
    void setBlue();
    State getState();
    boolean isFilled();

}
