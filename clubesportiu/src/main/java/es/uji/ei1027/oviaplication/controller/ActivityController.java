package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ActivityDao;
import es.uji.ei1027.oviaplication.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    private ActivityDao activityDao;

    @Autowired
    public void setActivityDao(ActivityDao activityDao){ this.activityDao = activityDao; }

    // Listar usuarios
    @RequestMapping("/listTodos")
    public String listActivitiesTodo(Model model) {
        model.addAttribute("activities", activityDao.getActivitiesTodo());
        model.addAttribute("instructorCounts", activityDao.getInstructorCounts());
        return "activity/listTodos";
    }

    @RequestMapping("/listPublicas")
    public String listActivitiesAcept(Model model) {
        model.addAttribute("activities", activityDao.getActivitiesAcept());
        return "activity/listPublicas";
    }

    @RequestMapping("/listInscripciones")
    public String listFutureActivitiesAcept(Model model) {
        model.addAttribute("activities", activityDao.getFutureActivitiesAcept());
        return "activity/listInscripciones";
    }

    @RequestMapping("/listPendientes")
    public String listActivitiesPend(Model model) {
        model.addAttribute("activities", activityDao.getActivitiesPend());
        return "activity/listPendientes";
    }

    @RequestMapping("/listMisActividadesAceptadasInstructor")
    public String listInstructorActivitiesAcept(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("activities", activityDao.getInstructorActivitiesAcept(user.getIdNumber()));
        return "activity/listMisActividadesAceptadasInstructor";
    }

    @RequestMapping("/listMisActividadesPendientesInstructor")
    public String lisInstructorActivitiesPend(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("activities", activityDao.getInstructorActivitiesPend(user.getIdNumber()));
        return "activity/listMisActividadesPendientesInstructor";
    }

    @RequestMapping("/listMisActividadesAceptadasFuturasInstructor")
    public String listInstructorFutureActivitiesAcept(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("activities", activityDao.getInstructorFutureActivitiesAcept(user.getIdNumber()));
        return "activity/listMisActividadesAceptadasFuturasInstructor";
    }


    @RequestMapping(value="/add")
    public String addActivity(Model model) {
        model.addAttribute("activity", new Activity());
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        return "activity/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, Model model) {
        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);
        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "activity/add";
        }
        activityDao.addActivity(activity);
        return "redirect:listTodos";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model) {
        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);
        return "activity/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id) {
        activityDao.deleteActivity(id); // Borra de la BD
        return "redirect:/activity/listTodos"; // Te devuelve a la lista
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int id) {
        model.addAttribute("activity", activityDao.getActivity(id));
        return "activity/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("activity") Activity activity,
            BindingResult bindingResult) {
        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors())
            return "activity/update";

        activityDao.updateAcivity(activity);
        return "redirect:listTodos";
    }

    @RequestMapping(value = "/accept/{id}")
    public String acceptActivity(@PathVariable int id) {
        activityDao.updateEstado(id, Estado.aceptado);
        return "redirect:/activity/listPendientes"; // Recarga la misma lista tras aceptar
    }

    @RequestMapping(value = "/reject/{id}")
    public String rejectActivity(@PathVariable int id) {
        activityDao.updateEstado(id, Estado.rechazado);
        return "redirect:/activity/listPendientes"; // Recarga la misma lista tras rechazar
    }

    // Mostrar los detalles completos de una actividad para el usuario
    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
    public String showActivityDetails(Model model, @PathVariable int id, HttpSession session) {
        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);
        UserDetails user = (UserDetails) session.getAttribute("user");
        model.addAttribute("user", user);
        return "activity/details";
    }

    @RequestMapping("/listMisActividades")
    public String listMyActivities(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getTipoUsuario().equals(TipoUsuario.instructor)){
            model.addAttribute("activities", activityDao.getMyActivities(user.getIdNumber(), user.getTipoUsuario()));
            return "activity/listMisActividadesInstructor";
        }
        model.addAttribute("activities", activityDao.getMyActivities(user.getIdNumber(), user.getTipoUsuario()));
        return "activity/listMisActividades";
    }

    @RequestMapping("/listMisActividadesFuturas")
    public String listMyFutureActivities(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("activities", activityDao.getMyFutureActivities(user.getIdNumber(), user.getTipoUsuario()));
        return "activity/listMisActividadesFuturas";
    }

}