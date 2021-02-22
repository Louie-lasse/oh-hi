import model.IModel;
import model.OriginalModel;

public class Application {
    public static void main(String[] args) {
        IModel model = new OriginalModel();
        model.createWorld(4);
    }
    int x = 0;
}
