package Controller;

import Model.IModel;
import Model.Position;
import Model.State;
import View.IView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class PositionController extends AnchorPane {
    private static IModel sharedModel;
    public static void setWorld(IModel model){ sharedModel = model; }
    private static IView parent;
    public static void setView(IView view){ parent = view; }

    private static boolean lockIsToggled = false;
    private static final String red = "-fx-background-color:  #C24A31";
    private static final String blue = "-fx-background-color:  #09CDDA";
    private static final String blank = "-fx-background-color:  #707070";
    private static final String gray = "-fx-background-color:  #A0A0A0";
    private static final String background = "-fx-background-color:  #505050";

    private final Position position;

    @FXML
    private AnchorPane colorPane;

    @FXML
    private ImageView lockImage;

    @FXML
    private AnchorPane cellPane;

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
        parent.cellActionPerformed(position);
    }

    public void clearLooks(){
        clearHelp();
    }

    public boolean isAt(Position position){
        return (this.position.equals(position));
    }
    
    private void clearHelp(){
        clearBorder();
    }

    public void displayHelp(){
        colorBorder();
    }

    public static void toggleLock(){
        lockIsToggled = !lockIsToggled;
    }

    public void displayLock(){
        if (!sharedModel.isLocked(position)) return;
        lockImage.setVisible(lockIsToggled);
    }

    public void updateColor(){
        setColor(sharedModel.getColor(position));
    }

    private void setColor(State color){
        switch (color.ordinal()) {
            case 1 -> colorRed();
            case 2 -> colorBlue();
            default -> colorClear();
        }
    }

    private void colorRed(){
        colorPane.setStyle(red);
    }

    private void colorBlue(){
        colorPane.setStyle(blue);
    }

    private void colorClear(){
        colorPane.setStyle(blank);
    }

    private void colorBorder(){
        cellPane.setStyle(gray);
    }

    private void clearBorder(){
        cellPane.setStyle(background);
    }

}
