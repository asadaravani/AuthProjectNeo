package kg.beganov.AuthProject.ecxeption;

public class UserNotVerifiedException extends RuntimeException{
    public UserNotVerifiedException() {
        super("User is not verified");
    }
}
