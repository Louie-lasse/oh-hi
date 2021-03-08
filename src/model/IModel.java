package model;

public interface IModel {
    void update();
    void createWorld(int size) throws IllegalWorldSizeException;
    void test();
    void nextState(Position position);
    boolean isCompleted();
    ICell[][] getWorld();
}
