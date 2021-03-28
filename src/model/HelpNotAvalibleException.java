package model;

import java.io.IOException;

public class HelpNotAvalibleException extends IOException {
    HelpNotAvalibleException(){
        super("Failed to get help");
    }
}
