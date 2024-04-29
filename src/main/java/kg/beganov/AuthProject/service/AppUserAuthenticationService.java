package kg.beganov.AuthProject.service;

import kg.beganov.AuthProject.DTO.AuthRequest;
import kg.beganov.AuthProject.DTO.AuthResponse;
import kg.beganov.AuthProject.DTO.RegisterRequest;
import kg.beganov.AuthProject.configuration.JWTUtil;
import kg.beganov.AuthProject.ecxeption.UserNotFoundException;
import kg.beganov.AuthProject.entity.AppUser;
import kg.beganov.AuthProject.entity.Role;
import kg.beganov.AuthProject.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserAuthenticationService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest authRequest){
        AppUser user = appUserRepository.findUserByEmail(authRequest.getEmail())
                .orElseThrow(UserNotFoundException::new);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );
        String userEmail = user.getEmail();
        var jwtToken = jwtUtil.generateToken(userEmail);
        System.out.println(jwtToken);
        return AuthResponse.builder().token(jwtToken).build();
    }
    public String register(RegisterRequest registerRequest){
        //check if all fields are valid
        if(appUserRepository.findUserByEmail(registerRequest.getEmail()).isPresent()){
            return "Email already exists";
        } else if (appUserRepository.findUserByUsername(registerRequest.getUsername()).isPresent()){
            return "Username is already taken";
            
        } else if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return "Passwords do not match";
        }

        AppUser user = new AppUser();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        return null;
    }

}
