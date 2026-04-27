package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/registrar")
public class RegistrarseController {

    private OVIUserDao oviUserDao;

    private PAP_PATIDao papPatiDao;

    @RequestMapping("/ovi")
    public String registrarOviUser(Model model){
        model.addAttribute("oviuser", new OVIUser());
        return "auth/registrar-ovi";
    }

    @RequestMapping("/pap")
    public String registrarPapPati(Model model){
        model.addAttribute("papPati", new PAP_PATI());
        return "auth/registrar-pap";
    }

    @RequestMapping(value="/ovi", method = RequestMethod.POST)
    public String processRegistrarOviUser(@ModelAttribute("oviuser") OVIUser oviUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "auth/registrar-ovi";
        oviUserDao.addOVIUser(oviUser);
        return "auth/login";
    }

    @RequestMapping(value="/pap", method = RequestMethod.POST)
    public String processRegistrarPapPati(@ModelAttribute("papPati") PAP_PATI papPati, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "auth/registrar-pap";
        papPatiDao.addPAP_PATI(papPati);
        return "auth/login";
    }
}
