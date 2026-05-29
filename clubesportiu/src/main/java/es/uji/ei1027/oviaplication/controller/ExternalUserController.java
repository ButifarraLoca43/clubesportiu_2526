package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ExternalUserDao;
import es.uji.ei1027.oviaplication.model.ExternalUser;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
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
@RequestMapping("/externaluser")
public class ExternalUserController {

    private ExternalUserDao externalUserDao;

    @Autowired
    public void setExternalUserDao(ExternalUserDao externalUserDao) {
        this.externalUserDao = externalUserDao;
    }

    // Listar todos los usuarios externos
    @RequestMapping("/list")
    public String listExternalUsers(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/externaluser/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("externalUsers", externalUserDao.getExternalUsers());
        return "externaluser/list";
    }

    // Mostrar formulario para editar
    @RequestMapping(value = "/update/{idnumber}", method = RequestMethod.GET)
    public String editExternalUser(Model model, @PathVariable String idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/externaluser/update/" + idnumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("externalUser", externalUserDao.getExternalUser(idnumber));
        return "externaluser/update";
    }

    // Procesar el formulario de edición
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("externalUser") ExternalUser externalUser,
                                      BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        ExternalUserValidator validator = new ExternalUserValidator();
        validator.validate(externalUser, bindingResult);

        if (bindingResult.hasErrors()) {
            return "externaluser/update";
        }

        externalUserDao.updateExternalUser(externalUser);
        return "redirect:/externaluser/list"; // CORREGIDO: Ruta absoluta
    }

    // Mostrar la pantalla de confirmación de borrado
    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable String idnumber, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/externaluser/delete/" + idnumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("externalUser", externalUserDao.getExternalUser(idnumber));
        return "externaluser/delete";
    }

    // Procesar el borrado
    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        externalUserDao.deleteExternalUser(idnumber);
        return "redirect:/externaluser/list";
    }
}