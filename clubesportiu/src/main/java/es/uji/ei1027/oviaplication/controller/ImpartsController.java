package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ExternalUserDao;
import es.uji.ei1027.oviaplication.dao.ImpartsDao;
import es.uji.ei1027.oviaplication.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/imparts")
public class ImpartsController {

    private ImpartsDao impartsDao;

    @Autowired
    public void setImpartsDao(ImpartsDao impartsDao) {
        this.impartsDao = impartsDao;
    }

    @RequestMapping("/list")
    public String listImparts(Model model) {
        model.addAttribute("imparts", impartsDao.getImparts());
        return "imparts/list";
    }

    @RequestMapping(value="/add")
    public String addImparts(Model model) {
        model.addAttribute("imparts", new Imparts());
        return "imparts/add";
    }

    // Procesar el formulario de añadir
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("imparts") Imparts imparts,
                                   BindingResult bindingResult) {
        ImpartsValidator validator = new ImpartsValidator();
        validator.validate(imparts, bindingResult);

        if (bindingResult.hasErrors()) {
            return "imparts/add";
        }

        impartsDao.addImparts(imparts);
        return "redirect:list";
    }

    // Mostrar la pantalla de confirmación de borrado
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model) {
        model.addAttribute("imparts", impartsDao.getImparts(id));
        return "imparts/delete";
    }

    // Procesar el borrado
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id) {
        impartsDao.deleteImparts(id);
        return "redirect:/imparts/list";
    }

    // Mostrar formulario para editar
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editImparts(Model model, @PathVariable int id) {
        model.addAttribute("imparts", impartsDao.getImparts(id));
        return "imparts/update";
    }

    // Procesar el formulario de edición
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("imparts") Imparts imparts,
                                      BindingResult bindingResult) {
        ImpartsValidator validator = new ImpartsValidator();
        validator.validate(imparts, bindingResult);

        if (bindingResult.hasErrors()) {
            return "imparts/update";
        }

        impartsDao.updateImparts(imparts);
        return "redirect:list";
    }

    // === MÉTODOS PARA APUNTARSE DESDE LA VISTA PÚBLICA ===

    @RequestMapping(value = "/request/{idActivity}")
    public String pedirActividad(@PathVariable int idActivity, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/instructor/panel";
        }

        try {
            Imparts imparts = new Imparts();
            imparts.setIdActivity(idActivity);

            imparts.setIdActivity(idActivity);
            imparts.setIdInstructor(user.getIdNumber());


            impartsDao.addImparts(imparts);
            return "redirect:/instructor/panel";
        } catch (Exception e) {
            return "redirect:/activity/listInscripciones?error=inscription_failed";
        }
    }

    // Mostrar el formulario para usuarios externos
    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.GET)
    public String showExternalForm(@PathVariable int idActivity, Model model) {
        model.addAttribute("idActivity", idActivity);
        model.addAttribute("externalUser", new ExternalUser());
        return "imparts/externo";
    }
}