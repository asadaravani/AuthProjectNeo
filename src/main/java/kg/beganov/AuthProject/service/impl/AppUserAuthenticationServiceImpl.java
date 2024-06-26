package kg.beganov.AuthProject.service.impl;

import kg.beganov.AuthProject.Dto.AuthRequest;
import kg.beganov.AuthProject.Dto.AuthResponse;
import kg.beganov.AuthProject.Dto.RegisterRequest;
import kg.beganov.AuthProject.configuration.JWTUtil;
import kg.beganov.AuthProject.ecxeption.*;
import kg.beganov.AuthProject.entity.ConfirmationToken;
import kg.beganov.AuthProject.service.ConfirmationTokenService;
import kg.beganov.AuthProject.service.EmailSenderService;
import kg.beganov.AuthProject.entity.AppUser;
import kg.beganov.AuthProject.entity.Role;
import kg.beganov.AuthProject.repository.AppUserRepository;
import kg.beganov.AuthProject.service.AppUserAuthenticationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserAuthenticationServiceImpl implements AppUserAuthenticationService {
    AppUserRepository appUserRepository;
    PasswordEncoder passwordEncoder;
    JWTUtil jwtUtil;
    AuthenticationManager authenticationManager;
    EmailSenderService emailSenderService;
    ConfirmationTokenService confirmationTokenService;
    String dropletApi = "165.22.72.60";

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) throws UserNotFoundException, UserNotVerifiedException, InvalidDataProvidedException {
        String emailToLowercase = authRequest.getEmail().toLowerCase();
        AppUser user = appUserRepository.findUserByEmail(emailToLowercase)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!user.isEmailVerified()){
            ConfirmationToken confirmationToken = new ConfirmationToken();
            String link = generateLink(confirmationToken, user);
            emailSenderService.send(
                    user.getEmail(),
                    emailSenderService.buildEmail(user.getUsername(), link));

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
            throw new InvalidDataProvidedException("Invalid credential data provided");
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
        emailSenderService.send(
                confirmedEmail,
                emailSenderService.buildEmail(registerRequest.getUsername(), link));
        return "We have sent you a link to verify your email, please check your email";
    }

    @Override
    public String confirmToken(String token) throws ConfirmationTokenExpiredException {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new InvalidDataProvidedException("token not found"));

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
        return "http://" +dropletApi+":8080/api/user/confirmToken?token=" + token;

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
