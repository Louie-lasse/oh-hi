package model;

public interface IModel {
    void update();
    void createWorld(int size);
    void nextState(Position position);
    boolean isCompleted();
}
