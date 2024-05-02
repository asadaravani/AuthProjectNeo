package kg.beganov.AuthProject.service;

import kg.beganov.AuthProject.DTO.RegisterRequest;
import kg.beganov.AuthProject.ecxeption.InvalidDataProvidedException;
import kg.beganov.AuthProject.ecxeption.UserAlreadyExistException;

public interface UserValidator {

    void isUserValid(RegisterRequest registerRequest) throws
            InvalidDataProvidedException, UserAlreadyExistException;
}
