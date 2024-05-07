package kg.beganov.AuthProject.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/mainPage")
@AllArgsConstructor
public class MainPage {
    @GetMapping
    public String mainPage() {
        return "Wassup duude";
    }
}
