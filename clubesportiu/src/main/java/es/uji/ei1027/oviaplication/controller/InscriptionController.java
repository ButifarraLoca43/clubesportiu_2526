package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ExternalUserDao;
import es.uji.ei1027.oviaplication.dao.InscriptionDao;
import es.uji.ei1027.oviaplication.model.ExternalUser;
import es.uji.ei1027.oviaplication.model.Inscription;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/inscription")
public class InscriptionController {

    private InscriptionDao inscriptionDao;
    private ExternalUserDao externalUserDao;
    private ExternalUserValidator externalUserValidator;

    @Autowired
    public void setInscriptionDao(InscriptionDao inscriptionDao) {
        this.inscriptionDao = inscriptionDao;
    }

    @Autowired
    public void setExternalUserDao(ExternalUserDao externalUserDao) {
        this.externalUserDao = externalUserDao;
    }

    @Autowired
    public void setExternalUserValidator(ExternalUserValidator externalUserValidator) {
        this.externalUserValidator = externalUserValidator;
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
        return "redirect:/inscription/list";
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
                return "redirect:/activity/details/" + idActivity + "?error=invalid_user_type";
            }

            inscriptionDao.addInscription(inscription);

            // Usamos FlashAttribute para que el mensaje aparezca en la página de destino (Details)
            redirectAttributes.addFlashAttribute("inscriptionSuccess", true);
            return "redirect:/activity/details/" + idActivity;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ya estás inscrito o ha habido un error.");
            return "redirect:/activity/details/" + idActivity;
        }
    }

    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.GET)
    public String showExternalForm(@PathVariable int idActivity, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user != null) {
            return "redirect:/activity/details/" + idActivity;
        }
        model.addAttribute("idActivity", idActivity);
        model.addAttribute("externalUser", new ExternalUser());
        return "inscription/externo";
    }


    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.POST)
    public String processExternalForm(@PathVariable int idActivity,
                                      @ModelAttribute("externalUser") ExternalUser extUser,
                                      BindingResult bindingResult,
                                      Model model) {

        // Limpieza y formato del DNI
        if (extUser.getIdnumber() != null) {
            extUser.setIdnumber(extUser.getIdnumber().trim().toUpperCase());
        }

        // Validación estructural básica (Campos vacíos, formatos incorrectos...)
        externalUserValidator.validate(extUser, bindingResult);

        // Control previo manual del correo electrónico
        if (extUser.getEmail() != null && !extUser.getEmail().trim().isEmpty()) {
            ExternalUser userByEmail = externalUserDao.getExternalUserByEmail(extUser.getEmail().trim());
            if (userByEmail != null && !userByEmail.getIdnumber().equalsIgnoreCase(extUser.getIdnumber())) {
                bindingResult.rejectValue("email", "duplicado", "Este correo electrónico ya está registrado con otro DNI.");
            }
        }

        // Si ya hay errores acumulados, volvemos a la vista inmediatamente
        if (bindingResult.hasErrors()) {
            model.addAttribute("idActivity", idActivity);
            return "inscription/externo";
        }

        try {
            ExternalUser usuarioExistente = externalUserDao.getExternalUser(extUser.getIdnumber());

            if (usuarioExistente == null) {
                externalUserDao.addExternalUser(extUser);
            } else {
                externalUserDao.updateExternalUser(extUser);
            }

            Inscription inscription = new Inscription();
            inscription.setIdactivity(idActivity);
            inscription.setIdext(extUser.getIdnumber());

            inscriptionDao.addInscription(inscription);
            model.addAttribute("inscriptionSuccess", true);
            model.addAttribute("idActivity", idActivity);
            return "inscription/externo";

        } catch (DuplicateKeyException e) {
            // Analizamos el mensaje nativo de la BD para saber qué campo falló realmente
            String databaseMessage = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";

            if (databaseMessage.contains("email")) {
                bindingResult.rejectValue("email", "duplicado", "Este correo electrónico ya está registrado con otro DNI.");
            } else {
                bindingResult.rejectValue("idnumber", "duplicado", "Este asistente ya se encuentra inscrito en esta actividad.");
            }

            model.addAttribute("idActivity", idActivity);
            return "inscription/externo";

        } catch (DataIntegrityViolationException e) {
            String databaseMessage = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";

            if (databaseMessage.contains("email")) {
                bindingResult.rejectValue("email", "duplicado", "Este correo electrónico ya está registrado con otro DNI.");
            } else {
                bindingResult.rejectValue("idnumber", "error", "Conflicto de integridad en los datos introducidos.");
            }

            model.addAttribute("idActivity", idActivity);
            return "inscription/externo";
        }
    }
}