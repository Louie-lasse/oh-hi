package View;

import Controller.PositionController;
import Model.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewFirst extends Application implements Initializable, IView{

    public static void main(String[] args){ launch(args); }

    IModel model = new OriginalModel();

    private final List<PositionController> children = new ArrayList<>();

    @FXML
    private AnchorPane window;

    @FXML
    private AnchorPane mainMenu;

    @FXML
    private AnchorPane gamePane;

    @FXML
    private GridPane cellGrid;

    @FXML
    private MenuItem closeGameButton;

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("App/resources/oh-hi");

        Parent root = FXMLLoader.load(getClass().getResource("textView.fxml"), bundle);

        Scene scene = new Scene(root, 400, 500);

        stage.setTitle(bundle.getString("application.name"));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        PositionController.setWorld(model);
        PositionController.setView(this);
    }

    private int x = 4;

    @FXML
    void buttonPressed(ActionEvent event) {
        createGrid(x);
        gamePane.toFront();
        x += 2;
        closeGameButton.setDisable(false);
    }

    public void createGrid(int size) throws IllegalWorldSizeException {
        model.createWorld(size);
        cellGrid.getChildren().clear();
        this.children.clear();
        //TODO fix constraints. Alternatively, just do it statically
        PositionIterator iterator = new PositionIterator(size);
        while (iterator.hasNext()){
            addController(iterator.getNext());
        }
    }

    private void addController(Position position){
        PositionController newController = new PositionController(position);
        children.add(newController);
        cellGrid.add(newController, position.column, position.row);
    }

    @FXML
    void closeGame(ActionEvent event) {
        mainMenu.toFront();
        closeGameButton.setDisable(true);
    }

    @FXML
    protected void closeAboutActionPerformed(ActionEvent event) throws IOException {
        Stage gameStage = (Stage) mainMenu.getScene().getWindow();
        gameStage.hide();
    }


    @FXML
    void getHelp(MouseEvent event) {
        try{
            Position helpPosition = model.help();
            displayHelp(helpPosition);
        } catch (HelpNotAvailableException h){

        }
    }

    private void displayHelp(Position position){
        for (PositionController child: children){
            child.clearLooks();
            if (child.isAt(position)){
                child.displayHelp();
            }
        }
    }

    public void cellActionPerformed(Position position){
        if (model.isLocked(position)) toggleAllLocks();
        else {
            clearLooks();
        }
    }

    private void toggleAllLocks(){
        PositionController.toggleLock();
        for (PositionController positionController: children){
            positionController.displayLock();
        }
    }

    private void clearLooks(){
        for (PositionController child: children){
            child.clearLooks();
        }
    }

}
