package kg.beganov.AuthProject.controller;

import kg.beganov.AuthProject.DTO.AuthRequest;
import kg.beganov.AuthProject.DTO.RegisterRequest;
import kg.beganov.AuthProject.ecxeption.ConfirmationTokenExpiredException;
import kg.beganov.AuthProject.ecxeption.InvalidDataProvidedException;
import kg.beganov.AuthProject.ecxeption.UserNotFoundException;
import kg.beganov.AuthProject.ecxeption.UserNotVerifiedException;
import kg.beganov.AuthProject.service.AppUserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@AllArgsConstructor
public class AppUserAuthenticationController {
    private final AppUserAuthenticationService appUserAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest){
        try {
            return ResponseEntity.ok().body(appUserAuthenticationService.authenticate(authRequest));
        }catch (UserNotFoundException | UserNotVerifiedException | InvalidDataProvidedException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        try {
            return ResponseEntity.ok().body(appUserAuthenticationService.register(registerRequest));
        }catch (InvalidDataProvidedException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/confirmToken")
    public ResponseEntity<?> confirmEmail(@RequestParam String token){
        try {
            return ResponseEntity.ok().body(appUserAuthenticationService.confirmToken(token));
        }catch (ConfirmationTokenExpiredException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
