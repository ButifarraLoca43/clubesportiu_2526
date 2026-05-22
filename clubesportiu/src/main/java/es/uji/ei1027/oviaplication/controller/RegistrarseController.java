package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.DiversityType;
import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/registrar")
public class RegistrarseController {

    private OVIUserDao oviUserDao;

    private PAP_PATIDao papPatiDao;

    @Autowired
    public void setDaos(OVIUserDao oviUserDao, PAP_PATIDao papPatiDao) {
        this.oviUserDao = oviUserDao;
        this.papPatiDao = papPatiDao;
    }

    @RequestMapping("/ovi")
    public String registrarOviUser(Model model){
        model.addAttribute("oviuser", new OVIUser());
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        return "auth/registrar-ovi";
    }

    @RequestMapping("/pap")
    public String registrarPapPati(Model model){
        model.addAttribute("pap_pati", new PAP_PATI());
        return "auth/registrar-pap";
    }

    @RequestMapping(value="/ovi", method = RequestMethod.POST)
    public String processRegistrarOviUser(@ModelAttribute("oviuser") OVIUser oviUser, BindingResult bindingResult, Model model) {
        OVIUserValidator oviUserValidator = new OVIUserValidator();
        oviUserValidator.validate(oviUser, bindingResult);
        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "auth/registrar-ovi";
        }
        String nombreUsuario = oviUser.getUserName();

        if (oviUserDao.getOVIUserByUsername(nombreUsuario) != null) {
            bindingResult.rejectValue("userName", "error.oviuser", "Este nombre de usuario ya está registrado como usuario OVI.");

            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "auth/registrar-ovi";
        }

        if (papPatiDao.getPAP_PATIByUsername(nombreUsuario) != null) {
            bindingResult.rejectValue("userName", "error.oviuser", "Este nombre de usuario ya está registrado como PAP/PATI.");

            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "auth/registrar-ovi";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(oviUser.getUserPassword());
        oviUser.setUserPassword(encryptedPassword);
        oviUser.setEstado(Estado.pendiente);
        oviUserDao.addOVIUser(oviUser);
        return "redirect:/login";
    }

    @RequestMapping(value="/pap", method = RequestMethod.POST)
    public String processRegistrarPapPati(@ModelAttribute("pap_pati") PAP_PATI pap_pati, BindingResult bindingResult) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "auth/registrar-pap";

        String nombreUsuario = pap_pati.getUserName();

        if (papPatiDao.getPAP_PATIByUsername(nombreUsuario) != null) {
            bindingResult.rejectValue("userName", "error.pap_pati", "Este nombre de usuario ya está registrado como PAP/PATI.");
            return "auth/registrar-pap";
        }

        if (oviUserDao.getOVIUserByUsername(nombreUsuario) != null) {
            bindingResult.rejectValue("userName", "error.pap_pati", "Este nombre de usuario ya está registrado como usuario OVI.");
            return "auth/registrar-pap";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(pap_pati.getUserPassword());
        pap_pati.setUserPassword(encryptedPassword);
        papPatiDao.addPAP_PATI(pap_pati);
        return "redirect:/login";
    }


}
