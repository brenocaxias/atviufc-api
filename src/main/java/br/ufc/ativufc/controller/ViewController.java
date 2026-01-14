package br.ufc.ativufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String root() {
        // Quando acessar a raiz, abre o index.html que está na pasta static
        return "forward:/index.html";
    }
    
    // Opcional: Garante que /login também aponte para o arquivo certo
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }
}