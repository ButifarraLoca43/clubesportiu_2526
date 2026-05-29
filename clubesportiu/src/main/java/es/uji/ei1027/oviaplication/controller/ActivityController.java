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

    @RequestMapping("/listFormacion")
    public String listActivitiesFormacion(Model model) {
        model.addAttribute("instructorCounts", activityDao.getInstructorCounts());
        model.addAttribute("activities", activityDao.getActivitiesFormacion());
        return "activity/listFormacion";
    }

    @RequestMapping("/listDivulgacion")
    public String listActivitiesDivulgacion(Model model) {
        model.addAttribute("instructorCounts", activityDao.getInstructorCounts());
        model.addAttribute("activities", activityDao.getActivitiesDivulgacion());
        return "activity/listDivulgacion";
    }


    @RequestMapping(value="/add")
    public String addActivity(Model model) {
        model.addAttribute("activity", new Activity());
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        List<TipoActividad> listaTipos = Arrays.asList(TipoActividad.values());
        model.addAttribute("typeList", listaTipos);
        return "activity/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, Model model) {
        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);
        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            List<TipoActividad> listaTipos = Arrays.asList(TipoActividad.values());
            model.addAttribute("typeList", listaTipos);
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
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        List<TipoActividad> listaTipos = Arrays.asList(TipoActividad.values());
        model.addAttribute("typeList", listaTipos);
        return "activity/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("activity") Activity activity,
            BindingResult bindingResult, Model model) {
        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            List<TipoActividad> listaTipos = Arrays.asList(TipoActividad.values());
            model.addAttribute("typeList", listaTipos);
            return "activity/update";
        }

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
        if (activity.getTipo() != null && String.valueOf(activity.getTipo()).equalsIgnoreCase("FORMACION")) {
            int inscritosTotales = activityDao.getInscritosByActivityId(id).size();
            int plazasDisponibles = activity.getCapacity() - inscritosTotales;
            model.addAttribute("plazasDisponibles", Math.max(plazasDisponibles, 0));
        }
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

    @RequestMapping("/inscritos/{id}")
    public String verInscritos(@PathVariable("id") int id, Model model) {
        // 1. Buscamos los datos de la actividad (para poner el nombre en el título del HTML)
        // Nota: Asegúrate de que el método de tu DAO para buscar una actividad por ID se llama así (ej: getActivity o getActivityById)
        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);

        // 2. Buscamos la lista de personas inscritas usando el DAO
        model.addAttribute("inscritos", activityDao.getInscritosByActivityId(id));

        return "activity/listInscritos";
    }
}