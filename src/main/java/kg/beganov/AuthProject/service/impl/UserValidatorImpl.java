package kg.beganov.AuthProject.service.impl;

import kg.beganov.AuthProject.Dto.RegisterRequest;
import kg.beganov.AuthProject.ecxeption.InvalidDataProvidedException;
import kg.beganov.AuthProject.ecxeption.UserAlreadyExistException;
import kg.beganov.AuthProject.repository.AppUserRepository;
import kg.beganov.AuthProject.service.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserValidatorImpl implements UserValidator {

    private final AppUserRepository appUserRepository;

    @Override
    public void isUserValid(RegisterRequest registerRequest) throws
            InvalidDataProvidedException, UserAlreadyExistException {
        if(isEmailTaken(registerRequest.getEmail())){
            throw new UserAlreadyExistException("Email is already registered. Just log in dude");
        }
        if(isUsernameTaken(registerRequest.getUsername())){
            throw new UserAlreadyExistException("Username is already taken");
        }
        if(!isCredentialsValid(
                registerRequest.getEmail(),
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword())){
            throw new InvalidDataProvidedException("Invalid data provided");
        }
    }
    public boolean isCredentialsValid(String email, String username, String password, String confirmPassword){

        boolean isEmailValid = email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}");
        boolean isUsernameValid = username.matches("[a-zA-Z]+");
        boolean isPasswordValid = password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()-_+=]).{8,15}$");
        boolean passwordsMatch = password.equals(confirmPassword);
        return isEmailValid && isUsernameValid && isPasswordValid && passwordsMatch;
    }

    public boolean isEmailTaken(String email){
        System.out.println(appUserRepository.existsByEmail(email));
        return appUserRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username){
        System.out.println(appUserRepository.existsByUsername(username));
        return appUserRepository.existsByUsername(username);
    }
}
