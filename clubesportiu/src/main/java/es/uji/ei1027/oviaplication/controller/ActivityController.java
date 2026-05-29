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

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    private ActivityDao activityDao;

    @Autowired
    public void setActivityDao(ActivityDao activityDao){ this.activityDao = activityDao; }

    // ==========================================
    // 1. ZONA PÚBLICA (Acceso libre para todos)
    // ==========================================

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

    @RequestMapping(value = "/details/{id}", method = RequestMethod.GET)
    public String showActivityDetails(Model model, @PathVariable int id, HttpSession session) {
        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);

        // Aquí pasamos el usuario a la vista solo si existe (para mostrar o no el botón de inscribirse)
        UserDetails user = (UserDetails) session.getAttribute("user");
        model.addAttribute("user", user);

        if (activity.getTipo() != null && String.valueOf(activity.getTipo()).equalsIgnoreCase("FORMACION")) {
            int inscritosTotales = activityDao.getInscritosByActivityId(id).size();
            int plazasDisponibles = activity.getCapacity() - inscritosTotales;
            model.addAttribute("plazasDisponibles", Math.max(plazasDisponibles, 0));
        }
        return "activity/details";
    }


    // ==========================================
    // 2. ZONA PRIVADA USUARIO (OVI, PAP, Instructor...)
    // ==========================================

    @RequestMapping("/listMisActividades")
    public String listMyActivities(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listMisActividades");
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
            session.setAttribute("nextUrl", "/activity/listMisActividadesFuturas");
            return "redirect:/login";
        }

        model.addAttribute("activities", activityDao.getMyFutureActivities(user.getIdNumber(), user.getTipoUsuario()));
        return "activity/listMisActividadesFuturas";
    }


    // ==========================================
    // 3. ZONA INSTRUCTOR (Solo Instructores)
    // ==========================================

    @RequestMapping("/listMisActividadesAceptadasInstructor")
    public String listInstructorActivitiesAcept(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listMisActividadesAceptadasInstructor");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.instructor) return "/auth/acceso-denegado";

        model.addAttribute("activities", activityDao.getInstructorActivitiesAcept(user.getIdNumber()));
        return "activity/listMisActividadesAceptadasInstructor";
    }

    @RequestMapping("/listMisActividadesPendientesInstructor")
    public String lisInstructorActivitiesPend(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listMisActividadesPendientesInstructor");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.instructor) return "/auth/acceso-denegado";

        model.addAttribute("activities", activityDao.getInstructorActivitiesPend(user.getIdNumber()));
        return "activity/listMisActividadesPendientesInstructor";
    }

    @RequestMapping("/listMisActividadesAceptadasFuturasInstructor")
    public String listInstructorFutureActivitiesAcept(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listMisActividadesAceptadasFuturasInstructor");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.instructor) return "/auth/acceso-denegado";

        model.addAttribute("activities", activityDao.getInstructorFutureActivitiesAcept(user.getIdNumber()));
        return "activity/listMisActividadesAceptadasFuturasInstructor";
    }


    // ==========================================
    // 4. ZONA GESTIÓN / TÉCNICO (CRUD, Aprobar, Rechazar)
    // ==========================================

    @RequestMapping("/listTodos")
    public String listActivitiesTodo(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listTodos");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("activities", activityDao.getActivitiesTodo());
        model.addAttribute("instructorCounts", activityDao.getInstructorCounts());
        return "activity/listTodos";
    }

    @RequestMapping("/listPendientes")
    public String listActivitiesPend(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/listPendientes");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("activities", activityDao.getActivitiesPend());
        return "activity/listPendientes";
    }

    @RequestMapping(value="/add")
    public String addActivity(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("activity", new Activity());
        model.addAttribute("diversityList", Arrays.asList(DiversityType.values()));
        model.addAttribute("typeList", Arrays.asList(TipoActividad.values()));
        return "activity/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("diversityList", Arrays.asList(DiversityType.values()));
            model.addAttribute("typeList", Arrays.asList(TipoActividad.values()));
            return "activity/add";
        }
        activityDao.addActivity(activity);
        return "redirect:listTodos";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String showDeleteConfirm(@PathVariable int id, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/delete/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);
        return "activity/delete";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        activityDao.deleteActivity(id);
        return "redirect:/activity/listTodos";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/update/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("activity", activityDao.getActivity(id));
        model.addAttribute("diversityList", Arrays.asList(DiversityType.values()));
        model.addAttribute("typeList", Arrays.asList(TipoActividad.values()));
        return "activity/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("diversityList", Arrays.asList(DiversityType.values()));
            model.addAttribute("typeList", Arrays.asList(TipoActividad.values()));
            return "activity/update";
        }

        activityDao.updateAcivity(activity);
        return "redirect:listTodos";
    }

    @RequestMapping(value = "/accept/{id}")
    public String acceptActivity(@PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        activityDao.updateEstado(id, Estado.aceptado);
        return "redirect:/activity/listPendientes";
    }

    @RequestMapping(value = "/reject/{id}")
    public String rejectActivity(@PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        activityDao.updateEstado(id, Estado.rechazado);
        return "redirect:/activity/listPendientes";
    }

    @RequestMapping("/inscritos/{id}")
    public String verInscritos(@PathVariable("id") int id, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/activity/inscritos/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        Activity activity = activityDao.getActivity(id);
        model.addAttribute("activity", activity);
        model.addAttribute("inscritos", activityDao.getInscritosByActivityId(id));

        return "activity/listInscritos";
    }
}