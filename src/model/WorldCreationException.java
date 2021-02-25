package model;

public class WorldCreationException extends RuntimeException{
    WorldCreationException(){super("Failed to create word due to contradicting proofs");}
}
