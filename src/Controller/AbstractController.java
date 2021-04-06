package Controller;

import Model.IModel;

public abstract class AbstractController {
    static private IModel sharedModel;
    public static void setWorld(IModel model){ sharedModel = model; }
    public abstract void onClick();
}
