package Controller;

import Model.IModel;
import Model.Position;
import Model.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import java.io.IOException;
import java.util.ResourceBundle;

public class PositionController extends AnchorPane {
    static private IModel sharedModel;
    public static void setWorld(IModel model){ sharedModel = model; }

    private final Position position;
    private static final String red = "-fx-background-color:  #C24A31";
    private static final String blue = "-fx-background-color:  #09CDDA";
    private static final String blank = "-fx-background-color:  #505050";

    @FXML
    private AnchorPane colorPane;

    public PositionController(Position position){
        this.position = position;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CellController.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        updateColor();

    }


    @FXML
    void onClick(MouseEvent event) {
        sharedModel.nextState(position);
        updateColor();
    }

    private void updateColor(){
        setColor(sharedModel.getColor(position));
    }

    private void setColor(State color){
        switch (color.ordinal()) {
            case 1 -> colorRed();
            case 2 -> colorBlue();
            default -> clear();
        }
    }

    private void colorRed(){
        colorPane.setStyle(red);
    }

    private void colorBlue(){
        colorPane.setStyle(blue);
    }

    private void clear(){
        colorPane.setStyle(blank);
    }

}
