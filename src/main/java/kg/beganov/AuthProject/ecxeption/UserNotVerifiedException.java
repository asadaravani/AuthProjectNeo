package kg.beganov.AuthProject.ecxeption;

public class UserNotVerifiedException extends RuntimeException{
    public UserNotVerifiedException() {
        super("User is not verified");
    }
    public UserNotVerifiedException(String message) {
        super(message);
    }
}
