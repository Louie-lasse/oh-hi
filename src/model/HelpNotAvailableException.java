package model;

import java.io.IOException;

public class HelpNotAvailableException extends IOException {
    HelpNotAvailableException(){
        super("Failed to get help");
    }
}
