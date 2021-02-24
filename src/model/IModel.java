package model;

public interface IModel {
    void update();
    void createWorld(int size) throws IllegalWorldSizeException;
    void nextState(Position position);
    boolean isCompleted();
    ICell[][] getWorld();
}
