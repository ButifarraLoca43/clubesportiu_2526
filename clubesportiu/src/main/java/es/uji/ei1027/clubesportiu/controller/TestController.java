package es.uji.ei1027.clubesportiu.controller;

import es.uji.ei1027.clubesportiu.categoria.Categoria;
import es.uji.ei1027.clubesportiu.dao.NadadorDao;
import es.uji.ei1027.clubesportiu.model.Nadador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    private Categoria categoria;
    @Autowired
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @RequestMapping("/testCategoria")
    public String testCategoria(Model model) {
        int edat = 19;
        model.addAttribute("message",
                "Els nadadors de " + edat + " anys tenen la categoria " +
                        categoria.getCategoria(edat));
        return "test";
    }

    @RequestMapping("/test")
    public String testWeb(Model model ) {
        String message = "Provant la Web del Club Esportiu";
        model.addAttribute("message", message);
        return "test";
    }
    @Autowired
    NadadorDao nadadorDao;

    @RequestMapping("/testNadador/{nom}")
    public String testUnNadador(Model model, @PathVariable String nom) {
        Nadador nadador = nadadorDao.getNadador(nom);
        model.addAttribute("nadador", nadador);
        model.addAttribute("categoria", categoria.getCategoria(nadador.getEdat()));
        return "test_nadador";
    }



}
