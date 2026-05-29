package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ExternalUserDao;
import es.uji.ei1027.oviaplication.dao.InscriptionDao;
import es.uji.ei1027.oviaplication.model.ExternalUser;
import es.uji.ei1027.oviaplication.model.Inscription;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Añadido para gestionar errores en redirects

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/inscription")
public class InscriptionController {

    private InscriptionDao inscriptionDao;
    private ExternalUserDao externalUserDao;

    @Autowired
    public void setInscriptionDao(InscriptionDao inscriptionDao) {
        this.inscriptionDao = inscriptionDao;
    }

    @Autowired
    public void setExternalUserDao(ExternalUserDao externalUserDao) {
        this.externalUserDao = externalUserDao;
    }

    @RequestMapping("/list")
    public String listInscriptions(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/inscription/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("inscriptions", inscriptionDao.getInscriptions());
        return "inscription/list";
    }

    @RequestMapping(value="/add")
    public String addInscription(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/inscription/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("inscription", new Inscription());
        return "inscription/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("inscription") Inscription inscription, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        InscriptionValidator validator = new InscriptionValidator();
        validator.validate(inscription, bindingResult);

        if (bindingResult.hasErrors()) {
            return "inscription/add";
        }

        inscriptionDao.addInscription(inscription);
        return "redirect:/inscription/list";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/inscription/delete/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("inscription", inscriptionDao.getInscription(id));
        return "inscription/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        inscriptionDao.deleteInscription(id);
        return "redirect:/inscription/list";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editInscription(Model model, @PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/inscription/update/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("inscription", inscriptionDao.getInscription(id));
        return "inscription/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("inscription") Inscription inscription, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        InscriptionValidator validator = new InscriptionValidator();
        validator.validate(inscription, bindingResult);

        if (bindingResult.hasErrors()) {
            return "inscription/update";
        }

        inscriptionDao.updateInscription(inscription);
        return "redirect:/inscription/list"; // Corregido a ruta absoluta
    }


    @RequestMapping(value = "/apuntarse/{idActivity}")
    public String apuntarse(@PathVariable int idActivity, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            return "redirect:/inscription/externo/" + idActivity;
        }

        try {
            Inscription inscription = new Inscription();
            inscription.setIdactivity(idActivity);

            if (user.getTipoUsuario() == TipoUsuario.OVIUser) {
                inscription.setIdovi(user.getIdNumber());
            } else if (user.getTipoUsuario() == TipoUsuario.PAP_PATI) {
                inscription.setIdpap(user.getIdNumber());
            } else {
                redirectAttributes.addFlashAttribute("error", "Tipo de usuario no válido para inscripción rápida.");
                return "redirect:/activity/listInscripciones?error=invalid_user_type";
            }

            if (inscription.getIdovi() == null && inscription.getIdpap() == null) {
                redirectAttributes.addFlashAttribute("error", "No se pudo obtener tu identificador único.");
                return "redirect:/activity/listInscripciones?error=no_user_id";
            }

            inscriptionDao.addInscription(inscription);
            return "redirect:/activity/listInscripciones";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la inscripción: " + e.getMessage());
            return "redirect:/activity/listInscripciones?error=inscription_failed";
        }
    }

    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.GET)
    public String showExternalForm(@PathVariable int idActivity, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user != null) {
            // Corregido: la concatenación de la variable para evitar problemas de resolución de cadenas en Spring
            return "redirect:/activity/details/" + idActivity;
        }
        model.addAttribute("idActivity", idActivity);
        model.addAttribute("externalUser", new ExternalUser());
        return "inscription/externo";
    }

    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.POST)
    public String processExternalForm(@PathVariable int idActivity, @ModelAttribute("externalUser") ExternalUser extUser) {

        ExternalUser usuarioExistente = externalUserDao.getExternalUser(extUser.getIdnumber());

        if (usuarioExistente == null) {
            externalUserDao.addExternalUser(extUser);
        }

        Inscription inscription = new Inscription();
        inscription.setIdactivity(idActivity);
        inscription.setIdext(extUser.getIdnumber());

        inscriptionDao.addInscription(inscription);

        return "redirect:/activity/listInscripciones";
    }
}