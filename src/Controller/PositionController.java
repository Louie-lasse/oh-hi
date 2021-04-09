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
import java.util.ArrayList;
import java.util.List;

public class PositionController extends AnchorPane {
    static private IModel sharedModel;
    public static void setWorld(IModel model){ sharedModel = model; }

    private static boolean lockIsToggled = false;
    private static final String red = "-fx-background-color:  #C24A31";
    private static final String blue = "-fx-background-color:  #09CDDA";
    private static final String blank = "-fx-background-color:  #707070";
    private static final String gray = "-fx-background-color:  #A0A0A0";
    private static final String background = "-fx-background-color:  #505050";

    private static final List<PositionController> siblings = new ArrayList<>();
    //TODO refactor siblings to parent to make controller fit MVC (slim controller)
    //TODO refactor lockToggle to parent, as it's a view element (and not slim controller)
    //TODO get nicer lock image

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
        siblings.add(this);
    }

    public static void clear(){
        siblings.clear();
    }

    @FXML
    void onClick(MouseEvent event) {
        if (sharedModel.isLocked(position)) toggleAllLocks();
        else {
            sharedModel.nextState(position);
            updateColor();
            clearLooks();
        }
    }

    private static void toggleAllLocks(){
        lockIsToggled = !lockIsToggled;
        for (PositionController positionController: siblings){
            positionController.toggleLock();
        }
    }

    public static void clearLooks(){
        clearHelp();
    }
    
    private static void clearHelp(){
        for (PositionController positionController: siblings)
            positionController.clearBorder();
    }

    public static void help(Position position){
        clearHelp();
        for (PositionController controller: siblings){
            if (controller.position.equals(position)){
                controller.displayHelp();
                break;
            }
        }
    }

    private void displayHelp(){
        colorBorder();
    }

    private void toggleLock(){
        if (!sharedModel.isLocked(position)) return;
        if(lockIsToggled){
            lockImage.toFront();
        } else {
            lockImage.toBack();
        }
    }

    private void updateColor(){
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
