package kg.beganov.AuthProject.controller;

import kg.beganov.AuthProject.DTO.AuthRequest;
import kg.beganov.AuthProject.DTO.RegisterRequest;
import kg.beganov.AuthProject.service.AppUserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/user")
@AllArgsConstructor
public class AppUserAuthenticationController {
    private final AppUserAuthenticationService appUserAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(AuthRequest authRequest){
        return ResponseEntity.ok().body(appUserAuthenticationService.authenticate(authRequest));
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(RegisterRequest registerRequest){
        return ResponseEntity.ok().body(appUserAuthenticationService.register(registerRequest));
    }

}
