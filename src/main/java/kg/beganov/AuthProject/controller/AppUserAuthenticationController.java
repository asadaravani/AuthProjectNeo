package kg.beganov.AuthProject.controller;

import kg.beganov.AuthProject.Dto.AuthRequest;
import kg.beganov.AuthProject.Dto.RegisterRequest;
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
        return ResponseEntity.ok().body(appUserAuthenticationService.authenticate(authRequest));
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok().body(appUserAuthenticationService.register(registerRequest));
    }
    @GetMapping("/confirmToken")
    public ResponseEntity<?> confirmEmail(@RequestParam String token){
        return ResponseEntity.ok().body(appUserAuthenticationService.confirmToken(token));
    }

}
