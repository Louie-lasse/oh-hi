package model;

public interface IModel {
    void update();
    void createWorld(int size);
    void changeCell(Position position);
    boolean isCompleted();
}
