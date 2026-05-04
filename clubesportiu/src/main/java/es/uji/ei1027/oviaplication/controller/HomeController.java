package es.uji.ei1027.oviaplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/") // Esto atrapa tanto 'localhost:8080' como 'localhost:8080/'
    public String index() {
        return "index"; // Busca templates/index.html
    }
}
