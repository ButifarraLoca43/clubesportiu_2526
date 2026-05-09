package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.model.DiversityType;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/oviuser")
public class OVIUserController {

    private OVIUserDao oviUserDao;
    private MatchDao matchDao;

    @Autowired
    public void setOviUserDao(OVIUserDao oviUserDao){ this.oviUserDao = oviUserDao; }
     @Autowired
    public void setMatchDao(MatchDao matchDao){ this.matchDao = matchDao; }



    // Listar usuarios
    @RequestMapping("/list")
    public String listOVIUsers(Model model) {
        model.addAttribute("oviusers", oviUserDao.getOVIUsers());
        return "oviuser/list";
    }

    @RequestMapping(value="/add")
    public String addOVIUser(Model model) {
        model.addAttribute("oviuser", new OVIUser());
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        return "oviuser/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviuser") OVIUser user, BindingResult bindingResult, Model model) {
        OVIUserValidator oviUserValidator = new OVIUserValidator();
        oviUserValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "oviuser/add";
        }
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        user.setUserPassword(passwordEncryptor.encryptPassword(user.getUserPassword()));
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

    @RequestMapping("/panel")
    public String panel() {
        return "oviuser/panel";
    }

    @RequestMapping("/details/{id}")
    public String detailsOVIUser(Model model, @PathVariable String id) {
        model.addAttribute("oviuser", oviUserDao.getOVIUser(id));
        return "oviuser/details";
    }

    @RequestMapping("asignaciones")
    public String listAsignedRequest(Model model, HttpSession session){
        UserDetails userDetails = (UserDetails) session.getAttribute("user");
        if (userDetails != null) {
            OVIUser oviUser = oviUserDao.getOVIUserByUsername(userDetails.getUserName());
            String id = oviUser.getIdNumber();
            model.addAttribute("request", oviUserDao.getRequestsMatch(id));
        }

        return "oviuser/matchlist";
    }

    @RequestMapping("/listAsignaciones/{idrequest}")
    public String listAssignedPAPs(Model model, @PathVariable("idrequest") int idRequest) {
        List<Map<String, Object>> paps = oviUserDao.getPAPsByRequest(idRequest);

        model.addAttribute("paps", paps);
        model.addAttribute("idRequest", idRequest);

        return "oviuser/assigned_paps";
    }

    @RequestMapping(value = "/acceptMatch/{idRequest}/{idpap}")
    public String acceptMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap) {

        matchDao.updateEstado(idRequest, idpap, "pendiente_PAP");
        return "redirect:/oviuser/asignaciones";
    }


    @RequestMapping(value = "/rejectMatch/{idRequest}/{idpap}")
    public String rejectMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap) {

        matchDao.updateEstado(idRequest, idpap, "rechaza_OVI");
        return "redirect:/oviuser/asignaciones";
    }

    @RequestMapping("editar")
    public String editar(HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("user");
        if (userDetails != null) {
            OVIUser oviUser = oviUserDao.getOVIUserByUsername(userDetails.getUserName());
            return "redirect:/oviuser/update/" + oviUser.getIdNumber();
        }
        return "redirect:/oviuser/panel";
    }
}
