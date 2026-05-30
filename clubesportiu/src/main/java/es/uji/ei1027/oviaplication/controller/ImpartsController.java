package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ActivityDao;
import es.uji.ei1027.oviaplication.dao.ImpartsDao;
import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/imparts")
public class ImpartsController {

    private ImpartsDao impartsDao;
    private ActivityDao activityDao;
    private InstructorDao instructorDao;

    @Autowired
    public void setImpartsDao(ImpartsDao impartsDao) { this.impartsDao = impartsDao; }
    @Autowired
    public void setActivityDao(ActivityDao activityDao) { this.activityDao = activityDao; }
    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) { this.instructorDao = instructorDao; }


    @RequestMapping("/list")
    public String listImparts(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("imparts", impartsDao.getImparts());
        return "imparts/list";
    }

    @RequestMapping(value="/add")
    public String addImparts(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("imparts", new Imparts());
        return "imparts/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("imparts") Imparts imparts, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        ImpartsValidator validator = new ImpartsValidator();
        validator.validate(imparts, bindingResult);

        if (bindingResult.hasErrors()) {
            return "imparts/add";
        }

        impartsDao.addImparts(imparts);
        return "redirect:/imparts/list"; // Corregido a ruta absoluta
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/delete/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("imparts", impartsDao.getImparts(id));
        return "imparts/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        impartsDao.deleteImparts(id);
        return "redirect:/imparts/list";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editImparts(Model model, @PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/update/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("imparts", impartsDao.getImparts(id));
        return "imparts/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("imparts") Imparts imparts, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        ImpartsValidator validator = new ImpartsValidator();
        validator.validate(imparts, bindingResult);

        if (bindingResult.hasErrors()) {
            return "imparts/update";
        }

        impartsDao.updateImparts(imparts);
        return "redirect:/imparts/list"; // Corregido a ruta absoluta
    }


    @RequestMapping(value = "/request/{idActivity}")
    public String pedirActividad(@PathVariable int idActivity, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/request/" + idActivity);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.instructor) return "/auth/acceso-denegado";

        try {
            Imparts imparts = new Imparts();
            imparts.setIdActivity(idActivity);
            imparts.setIdInstructor(user.getIdNumber());
            impartsDao.addImparts(imparts);
            return "redirect:/activity/details/" + idActivity + "?requestSuccess=true";
        } catch (Exception e) {
            return "redirect:/activity/details/" + idActivity + "?error=true";
        }
    }

    @RequestMapping("/assign/{id}")
    public String showAssignPage(@PathVariable int id, Model model, HttpSession session,
                                 @RequestParam(required = false) Boolean actionSuccess,
                                 @RequestParam(required = false) String actionMessage) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/assign/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        Activity activity = activityDao.getActivity(id);
        if (activity == null) return "redirect:/activity/listTodos";

        model.addAttribute("activity", activity);
        model.addAttribute("requests", impartsDao.getInstructorRequestsByActivity(id));
        model.addAttribute("actionSuccess", actionSuccess);
        model.addAttribute("actionMessage", actionMessage);
        return "imparts/assign";
    }

    @RequestMapping("/accept/{idAct}/{idInst}")
    public String acceptInstructor(@PathVariable int idAct, @PathVariable String idInst, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        impartsDao.updateImpartsEstado(idAct, idInst, "aceptado");
        return "redirect:/imparts/assign/" + idAct + "?actionSuccess=true&actionMessage=Instructor+aceptado+correctamente";
    }

    @RequestMapping("/reject/{idAct}/{idInst}")
    public String rejectInstructor(@PathVariable int idAct, @PathVariable String idInst, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        impartsDao.updateImpartsEstado(idAct, idInst, "rechazado");
        return "redirect:/imparts/assign/" + idAct + "?actionSuccess=true&actionMessage=Instructor+rechazado+correctamente";
    }

    @RequestMapping("/info/{idInstructor}")
    public String showInstructorInfo(@PathVariable String idInstructor, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/imparts/info/" + idInstructor);
            return "redirect:/login";
        }

        // Seguridad: Solo el Técnico puede ver perfiles, o el propio Instructor viendo el suyo
        if (user.getTipoUsuario() != TipoUsuario.tecnico &&
                !(user.getTipoUsuario() == TipoUsuario.instructor && user.getIdNumber().equals(idInstructor))) {
            return "/auth/acceso-denegado";
        }

        Instructor instructor = instructorDao.getInstructor(idInstructor);
        if (instructor == null) {
            return "redirect:/activity/listTodos";
        }

        model.addAttribute("instructor", instructor);
        return "imparts/info";
    }
}