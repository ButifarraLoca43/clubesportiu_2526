package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ExternalUserDao;
import es.uji.ei1027.oviaplication.model.ExternalUser;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/externaluser")
public class ExternalUserController {

    private ExternalUserDao externalUserDao;
    private ExternalUserValidator externalUserValidator;

    @Autowired
    public void setExternalUserDao(ExternalUserDao externalUserDao) {
        this.externalUserDao = externalUserDao;
    }

    @Autowired
    public void setExternalUserValidator(ExternalUserValidator externalUserValidator) {
        this.externalUserValidator = externalUserValidator;
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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("externalUser") ExternalUser externalUser,
                                   BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        // 1. Normalizamos el DNI
        if (externalUser.getIdnumber() != null) {
            externalUser.setIdnumber(externalUser.getIdnumber().trim().toUpperCase());
        }

        // 2. Pasamos las validaciones base
        externalUserValidator.validate(externalUser, bindingResult);
        if (bindingResult.hasErrors()) {
            return "externaluser/add";
        }

        // 3. Comprobación cruzada de Email
        ExternalUser userByEmail = externalUserDao.getExternalUserByEmail(externalUser.getEmail());
        if (userByEmail != null && !userByEmail.getIdnumber().equals(externalUser.getIdnumber())) {
            bindingResult.rejectValue("email", "duplicado", "Este correo electrónico ya está registrado con otro DNI.");
            return "externaluser/add";
        }

        // 4. Bloque blindado: Operaciones con la Base de Datos
        try {
            ExternalUser asistenteExistente = externalUserDao.getExternalUser(externalUser.getIdnumber());

            if (asistenteExistente == null) {
                // Es nuevo, lo creamos
                externalUserDao.addExternalUser(externalUser);
            } else {
                // Ya existe en la base de datos, lo actualizamos
                externalUserDao.updateExternalUser(externalUser);
            }

            // =========================================================
            // IMPORTANTE: Si aquí tienes código que inscribe al usuario
            // en una tabla de actividades, déjalo dentro de este 'try'.
            // =========================================================

        } catch (DuplicateKeyException e) {
            // Si la BD se queja de que ya está en la actividad, lo atrapamos aquí y pintamos en rojo.
            bindingResult.rejectValue("idnumber", "duplicado", "Este DNI ya se encuentra inscrito en esta actividad.");
            return "externaluser/add";

        } catch (DataIntegrityViolationException e) {
            // Por si hay algún otro bloqueo de la BD (ej. teléfono duplicado)
            bindingResult.rejectValue("idnumber", "error", "Conflicto de datos: Comprueba que la información no esté duplicada.");
            return "externaluser/add";
        }

        return "redirect:/externaluser/list";
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

        model.addAttribute("externalUser", externalUserDao.getExternalUser(idnumber.trim().toUpperCase()));
        return "externaluser/update";
    }

    // Procesar el formulario de edición
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("externalUser") ExternalUser externalUser,
                                      BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        // Normalizamos el formato del DNI
        if (externalUser.getIdnumber() != null) {
            externalUser.setIdnumber(externalUser.getIdnumber().trim().toUpperCase());
        }

        externalUserValidator.validate(externalUser, bindingResult);
        if (bindingResult.hasErrors()) {
            return "externaluser/update";
        }

        externalUserDao.updateExternalUser(externalUser);
        return "redirect:/externaluser/list";
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

        model.addAttribute("externalUser", externalUserDao.getExternalUser(idnumber.trim().toUpperCase()));
        return "externaluser/delete";
    }

    // Procesar el borrado
    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        externalUserDao.deleteExternalUser(idnumber.trim().toUpperCase());
        return "redirect:/externaluser/list";
    }
}