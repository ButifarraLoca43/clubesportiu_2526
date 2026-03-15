package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.model.Nadador;
import es.uji.ei1027.oviaplication.model.OVIUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/oviuser")
public class OVIUserController {

    private OVIUserDao oviUserDao;

    @Autowired
    public void setOviUserDao(OVIUserDao oviUserDao){ this.oviUserDao = oviUserDao; }

    // Listar usuarios
    @RequestMapping("/list")
    public String listOVIUsers(Model model) {
        model.addAttribute("oviusers", oviUserDao.getOVIUsers());
        return "oviuser/list";
    }

    @RequestMapping(value="/add")
    public String addOVIUser(Model model) {
        model.addAttribute("oviuser", new OVIUser());
        return "oviuser/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviuser") OVIUser user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "oviuser/add";
        oviUserDao.addOVIUser(user);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDelete(@PathVariable String id) {
        oviUserDao.deleteOVIUser(id);
        return "redirect:../list";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editOVIUser(Model model, @PathVariable String id) {
        model.addAttribute("oviuser", oviUserDao.getOVIUser(id));
        return "oviuser/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("oviuser") OVIUser oviuser,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "oviuser/update";
        oviUserDao.updateOVIUser(oviuser);
        return "redirect:list";
    }
}
