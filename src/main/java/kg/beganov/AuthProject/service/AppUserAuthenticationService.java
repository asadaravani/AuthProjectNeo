package kg.beganov.AuthProject.service;

import kg.beganov.AuthProject.Dto.AuthRequest;
import kg.beganov.AuthProject.Dto.AuthResponse;
import kg.beganov.AuthProject.Dto.RegisterRequest;

public interface AppUserAuthenticationService{

    AuthResponse authenticate(AuthRequest authRequest);

    String register(RegisterRequest registerRequest);

    String confirmToken(String token);
}
