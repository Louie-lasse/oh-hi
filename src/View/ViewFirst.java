package View;

import Controller.PositionController;
import Model.IModel;
import Model.OriginalModel;
import Model.Position;
import Model.PositionIterator;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewFirst extends Application implements Initializable {

    public static void main(String[] args){ launch(args); }

    @FXML
    private AnchorPane window;

    @FXML
    private GridPane cellGrid4x4;

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
        IModel model = new OriginalModel();
        model.createWorld(4);
        PositionController.setWorld(model);
        PositionIterator iterator = new PositionIterator(4);
        Position position;
        while (iterator.hasNext()){
            position = iterator.getNext();
            cellGrid4x4.add(new PositionController(position), position.column, position.row);
        }
        App.Main.displayWorld(model.getWorld());
    }


}
