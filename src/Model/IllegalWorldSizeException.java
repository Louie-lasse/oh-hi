package Model;

public class IllegalWorldSizeException extends IllegalArgumentException{
    IllegalWorldSizeException(int size){
        super("Size "+size+" not allowed, as it is not divisible by 2");
    }

}
