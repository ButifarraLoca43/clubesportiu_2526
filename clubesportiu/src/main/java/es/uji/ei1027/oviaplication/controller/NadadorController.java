package es.uji.ei1027.oviaplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.oviaplication.dao.NadadorDao;
import es.uji.ei1027.oviaplication.model.Nadador;


@Controller
@RequestMapping("/nadador")
public class NadadorController {

    private NadadorDao nadadorDao;

    @Autowired
    public void setNadadorDao(NadadorDao nadadorDao) {
        this.nadadorDao = nadadorDao;
    }

    // Operacions: Crear, llistar, actualitzar, esborrar

    @RequestMapping("/list")
    public String listNadadors(Model model) {
        model.addAttribute("nadadors", nadadorDao.getNadadors());
        return "nadador/list";
    }

    @RequestMapping(value = "/add")
    public String addNadador(Model model) {
        model.addAttribute("nadador", new Nadador());
        return "nadador/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("nadador") Nadador nadador,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "nadador/add";
        nadadorDao.addNadador(nadador);
        return "redirect:list";
    }

    @RequestMapping(value = "/update/{nom}", method = RequestMethod.GET)
    public String editNadador(Model model, @PathVariable String nom) {
        model.addAttribute("nadador", nadadorDao.getNadador(nom));
        return "nadador/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("nadador") Nadador nadador,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "nadador/update";
        nadadorDao.updateNadador(nadador);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{nom}")
    public String processDelete(@PathVariable String nom) {
        nadadorDao.deleteNadador(nadadorDao.getNadador(nom));
        return "redirect:../list";
    }


}

