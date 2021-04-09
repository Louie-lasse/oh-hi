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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewFirst extends Application implements Initializable, IView{

    public static void main(String[] args){ launch(args); }

    IModel model = new OriginalModel();

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
    }

    private static int x = 4;

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
        PositionController.clear();
        //TODO fix constraints. Alternatively, just do it statically
        PositionIterator iterator = new PositionIterator(size);
        Position position;
        while (iterator.hasNext()){
            position = iterator.getNext();
            cellGrid.add(new PositionController(position), position.column, position.row);
        }
        //cellGrid.autosize();
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
            PositionController.help(helpPosition);
        } catch (HelpNotAvailableException h){

        }
    }

    public void cellActionPerformed(){
        PositionController.clearLooks();
    }

}
