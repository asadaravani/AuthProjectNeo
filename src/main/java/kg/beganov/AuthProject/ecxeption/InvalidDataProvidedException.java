package kg.beganov.AuthProject.ecxeption;

public class InvalidDataProvidedException extends RuntimeException{
    public InvalidDataProvidedException(String message) {
        super(message);
    }
}
