package kg.beganov.AuthProject.ecxeption;

public class ConfirmationTokenExpiredException extends BaseException{

    public ConfirmationTokenExpiredException(String message) {
        super(message);
    }
}
