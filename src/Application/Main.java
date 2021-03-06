package Application;

import model.ICell;
import model.IModel;
import model.OriginalModel;
import model.State;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        IModel model = new OriginalModel();
        model.createWorld(10);
        displayWorld(model.getWorld());
    }

    public static void displayWorld(ICell[][] world){
        for (ICell[] row: world){
            displayRow(row);
            out.println();
        }
        out.println("\n");
    }

    private static void displayRow(ICell[] row){
        for (ICell cell: row){
            out.print(cell.getState());
            out.print(", ");
            if (cell.getState() == State.RED){
                out.print(" ");
            }
        }
    }
}
