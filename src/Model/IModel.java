package Model;

public interface IModel {
    void update();
    void createWorld(int size) throws IllegalWorldSizeException;
    void nextState(Position position);
    boolean isCompleted();
    boolean isCorrect();
    ICell[][] getWorld();
    Position help() throws HelpNotAvailableException;
    int getSize();
    State getColor(Position position);
    boolean isLocked(Position position);
}
