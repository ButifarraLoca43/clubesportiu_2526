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

    // Inyectamos el DAO de usuarios externos para poder comprobar si existen o crearlos
    @Autowired
    public void setExternalUserDao(ExternalUserDao externalUserDao) {
        this.externalUserDao = externalUserDao;
    }

    // Listar todas las inscripciones
    @RequestMapping("/list")
    public String listInscriptions(Model model) {
        model.addAttribute("inscriptions", inscriptionDao.getInscriptions());
        return "inscription/list";
    }

    // Mostrar formulario para añadir inscripción
    @RequestMapping(value="/add")
    public String addInscription(Model model) {
        model.addAttribute("inscription", new Inscription());
        return "inscription/add";
    }

    // Procesar el formulario de añadir
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("inscription") Inscription inscription,
                                   BindingResult bindingResult) {
        InscriptionValidator validator = new InscriptionValidator();
        validator.validate(inscription, bindingResult);

        if (bindingResult.hasErrors()) {
            return "inscription/add";
        }

        inscriptionDao.addInscription(inscription);
        return "redirect:list";
    }

    // Mostrar la pantalla de confirmación de borrado
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model) {
        model.addAttribute("inscription", inscriptionDao.getInscription(id));
        return "inscription/delete";
    }

    // Procesar el borrado
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id) {
        inscriptionDao.deleteInscription(id);
        return "redirect:/inscription/list";
    }

    // Mostrar formulario para editar
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editInscription(Model model, @PathVariable int id) {
        model.addAttribute("inscription", inscriptionDao.getInscription(id));
        return "inscription/update";
    }

    // Procesar el formulario de edición
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("inscription") Inscription inscription,
                                      BindingResult bindingResult) {
        InscriptionValidator validator = new InscriptionValidator();
        validator.validate(inscription, bindingResult);

        if (bindingResult.hasErrors()) {
            return "inscription/update";
        }

        inscriptionDao.updateInscription(inscription);
        return "redirect:list";
    }

    // === MÉTODOS PARA APUNTARSE DESDE LA VISTA PÚBLICA ===

    @RequestMapping(value = "/apuntarse/{idActivity}")
    public String apuntarse(@PathVariable int idActivity, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            // Si no hay sesión, le mandamos a rellenar sus datos
            return "redirect:/inscription/externo/" + idActivity;
        }

        try {
            // Si hay sesión, preparamos la inscripción
            Inscription inscription = new Inscription();
            inscription.setIdactivity(idActivity);

            // Asignar el ID del usuario según su tipo
            if (user.getTipoUsuario() == TipoUsuario.OVIUser) {
                inscription.setIdovi(user.getIdNumber());
            } else if (user.getTipoUsuario() == TipoUsuario.PAP_PATI) {
                inscription.setIdpap(user.getIdNumber());
            } else {
                model.addAttribute("error", "Tipo de usuario no válido");
                return "redirect:/activity/listInscripciones?error=invalid_user_type";
            }

            // Validar que se haya asignado al menos un ID
            if (inscription.getIdovi() == null && inscription.getIdpap() == null) {
                model.addAttribute("error", "No se pudo obtener tu identificador");
                return "redirect:/activity/listInscripciones?error=no_user_id";
            }

            inscriptionDao.addInscription(inscription);
            return "redirect:/activity/listInscripciones";
        } catch (Exception e) {
            model.addAttribute("error", "Error al inscribirse: " + e.getMessage());
            return "redirect:/activity/listInscripciones?error=inscription_failed";
        }
    }

    // Mostrar el formulario para usuarios externos
    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.GET)
    public String showExternalForm(@PathVariable int idActivity, Model model) {
        model.addAttribute("idActivity", idActivity);
        model.addAttribute("externalUser", new ExternalUser());
        return "inscription/externo";
    }

    // Procesar el formulario de usuario externo (el que faltaba)
    @RequestMapping(value = "/externo/{idActivity}", method = RequestMethod.POST)
    public String processExternalForm(@PathVariable int idActivity,
                                      @ModelAttribute("externalUser") ExternalUser extUser) {

        // 1. Comprobar si el usuario externo ya existe en la BD por su DNI (idnumber)
        ExternalUser usuarioExistente = externalUserDao.getExternalUser(extUser.getIdnumber());

        if (usuarioExistente == null) {
            // No existe, así que lo guardamos en la tabla external_user
            externalUserDao.addExternalUser(extUser);
        }

        // 2. Crear la inscripción vinculada al usuario externo
        Inscription inscription = new Inscription();
        inscription.setIdactivity(idActivity);
        inscription.setIdext(extUser.getIdnumber()); // Asignamos su DNI al campo idext

        inscriptionDao.addInscription(inscription);

        // 3. Redirigir a la lista de actividades (puedes cambiarlo a una pantalla de éxito)
        return "redirect:/activity/listInscripciones";
    }
}