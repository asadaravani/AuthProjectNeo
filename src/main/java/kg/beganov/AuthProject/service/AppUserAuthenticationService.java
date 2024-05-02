package kg.beganov.AuthProject.service;

import kg.beganov.AuthProject.DTO.AuthRequest;
import kg.beganov.AuthProject.DTO.AuthResponse;
import kg.beganov.AuthProject.DTO.RegisterRequest;

public interface AppUserAuthenticationService{

    AuthResponse authenticate(AuthRequest authRequest);

    String register(RegisterRequest registerRequest);

    String confirmToken(String token);
}
