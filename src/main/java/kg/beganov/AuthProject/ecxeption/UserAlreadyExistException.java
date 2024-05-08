package kg.beganov.AuthProject.ecxeption;

public class UserAlreadyExistException extends BaseException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
