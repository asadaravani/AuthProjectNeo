package kg.beganov.AuthProject.service.impl;

import kg.beganov.AuthProject.DTO.AuthRequest;
import kg.beganov.AuthProject.DTO.AuthResponse;
import kg.beganov.AuthProject.DTO.RegisterRequest;
import kg.beganov.AuthProject.configuration.JWTUtil;
import kg.beganov.AuthProject.ecxeption.*;
import kg.beganov.AuthProject.entity.ConfirmationToken;
import kg.beganov.AuthProject.service.ConfirmationTokenService;
import kg.beganov.AuthProject.service.UserValidator;
import kg.beganov.AuthProject.service.EmailSender;
import kg.beganov.AuthProject.entity.AppUser;
import kg.beganov.AuthProject.entity.Role;
import kg.beganov.AuthProject.repository.AppUserRepository;
import kg.beganov.AuthProject.service.AppUserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserAuthenticationServiceImpl implements AppUserAuthenticationService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserValidator userValidator;
    @Value("${confirmation.domain-name}")
    private final String dropletApiDomain = "localhost";

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) throws UserNotFoundException, UserNotVerifiedException, InvalidDataProvidedException {
        String emailToLowercase = authRequest.getEmail().toLowerCase();
        AppUser user = appUserRepository.findUserByEmail(emailToLowercase)
                .orElseThrow(UserNotFoundException::new);
        if(!user.isEmailVerified()){
            ConfirmationToken confirmationToken = new ConfirmationToken();
            String link = generateLink(confirmationToken, user);
            emailSender.send(
                    user.getEmail(),
                    emailSender.buildEmail(user.getUsername(), link));

            throw new UserNotVerifiedException("You are not verified! You will get a new verification email");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            emailToLowercase,
                            authRequest.getPassword()
                    )
            );
        }catch (BadCredentialsException e){
            throw new InvalidDataProvidedException("Invalid email or password");
        }
        String userEmail = user.getEmail();
        var jwtToken = jwtUtil.generateToken(userEmail);
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public String register(RegisterRequest registerRequest){
        try {
            userValidator.isUserValid(registerRequest);
        }catch (InvalidDataProvidedException | UserAlreadyExistException e){
            return e.getMessage();
        }
        String confirmedEmail = registerRequest.getEmail().toLowerCase();
        AppUser appUser = new AppUser();
        appUser.setUsername(registerRequest.getUsername());
        appUser.setEmail(confirmedEmail);
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setRole(Role.USER);
        appUserRepository.save(appUser);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        String link = generateLink(confirmationToken, appUser);
        emailSender.send(
                confirmedEmail,
                emailSender.buildEmail(registerRequest.getUsername(), link));

        return "We have sent you a link to verify your email, please check your email";
    }

    @Override
    public String confirmToken(String token) throws ConfirmationTokenExpiredException {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new ConfirmationTokenExpiredException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenExpiredException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ConfirmationTokenExpiredException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserRepository.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "Confirmed";
    }
    private String generateLink(ConfirmationToken confirmationToken, AppUser appUser) {
        String token = UUID.randomUUID().toString();
        confirmationToken.setToken(token);
        confirmationToken.setAppUser(appUser);
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(5L));
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return "http://" +dropletApiDomain+":8080/api/user/confirmToken?token=" + token;
    }

}
