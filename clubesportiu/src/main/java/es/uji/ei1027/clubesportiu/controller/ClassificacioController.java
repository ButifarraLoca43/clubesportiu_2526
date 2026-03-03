package es.uji.ei1027.clubesportiu.controller;

import es.uji.ei1027.clubesportiu.dao.ClassificacioDao;
import es.uji.ei1027.clubesportiu.model.Classificacio;
import es.uji.ei1027.clubesportiu.model.Nadador;
import es.uji.ei1027.clubesportiu.services.ClassificacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/classificacio")
public class ClassificacioController {

    private ClassificacioDao classificacioDao;

    private ClassificacioService classificacioService;

    @Autowired
    public void setClassificacioDao(ClassificacioDao classificacioDao) {
        this.classificacioDao = classificacioDao;
    }

    @Autowired
    public void setClassificacioService(ClassificacioService                                                classificacioService) {
        this.classificacioService = classificacioService;
    }

    @RequestMapping("/perpais")
    public String listClsfPerPais(Model model) {
        model.addAttribute("classificacions",
                classificacioService.getClassificationByCountry("Duos Sincro"));
        return "classificacio/perpais";
    }


    @RequestMapping(value = "/delete/{nNadador}/{nProva}")
    public String processDeleteClassif(@PathVariable String nNadador,
                                       @PathVariable String nProva) {
        classificacioDao.deleteClassificacio(nNadador, nProva);
        return "redirect:../../list";
    }

    @RequestMapping(value = "/add")
    public String addClassificacio(Model model) {
        model.addAttribute("classificacio", new Classificacio());
        return "classificacio/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("classificacio") Classificacio classificacio, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "classificacio/add";
        classificacioDao.addClassificacio(classificacio);
        return "redirect:list";
    }

    @RequestMapping("/list")
    public String listNadadors(Model model) {
        model.addAttribute("classificacions", classificacioDao.getClassificacions());
        return "classificacio/list";
    }

    @RequestMapping(value = "/update/{nNadador}/{nProva}", method = RequestMethod.GET)
    public String editClasificacio(Model model, @PathVariable String nNadador, @PathVariable String nProva ) {
        model.addAttribute("classificacio", classificacioDao.getClassificacio(nNadador, nProva));
        return "classificacio/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("classificacio") Classificacio clasificacio,
            BindingResult bindingResult) {
        if(bindingResult.hasErrors())
            return "classificacio/update";
        classificacioDao.updateClassificacio(clasificacio);
        return "redirect:list";
    }



}
