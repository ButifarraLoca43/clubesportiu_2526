package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ActivityDao;
import es.uji.ei1027.oviaplication.dao.ImpartsDao;
import es.uji.ei1027.oviaplication.dao.InstructorDao;
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
    private ActivityDao activityDao;
    private InstructorDao instructorDao;

    @Autowired
    public void setImpartsDao(ImpartsDao impartsDao) {
        this.impartsDao = impartsDao;
    }
    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }
    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao = instructorDao;
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

    @RequestMapping("/assign/{id}")
    public String showAssignPage(@PathVariable int id, Model model) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/activity/listTodos";
        }

        model.addAttribute("activity", activity);
        model.addAttribute("requests", impartsDao.getInstructorRequestsByActivity(id));
        return "imparts/assign";
    }

    // Procesar la aceptación de un instructor
    @RequestMapping("/accept/{idAct}/{idInst}")
    public String acceptInstructor(@PathVariable int idAct, @PathVariable String idInst) {
        impartsDao.updateImpartsEstado(idAct, idInst, "aceptado");
        return "redirect:/imparts/assign/" + idAct;
    }

    // Procesar el rechazo/desasignación de un instructor
    @RequestMapping("/reject/{idAct}/{idInst}")
    public String rejectInstructor(@PathVariable int idAct, @PathVariable String idInst) {
        impartsDao.updateImpartsEstado(idAct, idInst, "rechazado");
        return "redirect:/imparts/assign/" + idAct;
    }

    // Mostrar el perfil completo de un instructor
// Mostrar el perfil completo de un instructor
    @RequestMapping("/info/{idInstructor}")
    public String showInstructorInfo(@PathVariable String idInstructor, Model model) {
        // Necesitas usar tu DAO de instructores/usuarios aquí
        Instructor instructor = instructorDao.getInstructor(idInstructor);

        if (instructor == null) {
            // Si el instructor no existe, lo devolvemos a la lista de actividades
            return "redirect:/activity/listTodos";
        }

        model.addAttribute("instructor", instructor);
        return "imparts/info";
    }
}