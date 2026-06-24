package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ImpartsDao;
import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/instructor")
public class InstructorController {

    private InstructorDao instructorDao;
    private InstructorValidator instructorValidator;
    private ImpartsDao impartsDao;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao){ this.instructorDao = instructorDao; }
    @Autowired
    public void setInstructorValidator(InstructorValidator instructorValidator){ this.instructorValidator = instructorValidator; }
    @Autowired
    public void setImpartsDao(ImpartsDao impartsDao){ this.impartsDao = impartsDao; }

    // ==========================================
    // VISTAS DE GESTIÓN DE BORRADO (CON COMPROBACIÓN)
    // ==========================================

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable String idNumber, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/delete/" + idNumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        String referer = request.getHeader("Referer");
        String urlOrigen = (referer != null) ? referer : "/instructor/list";

        // Comprobamos si tiene actividades impartidas
        List<?> actividades = impartsDao.getImpartsByInstructor(idNumber);

        if (actividades != null && !actividades.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeDenegado",
                    "No se puede eliminar a este instructor porque tiene actividades asignadas.");
            return "redirect:" + urlOrigen;
        }

        model.addAttribute("urlOrigen", urlOrigen);
        model.addAttribute("instructor", instructor);
        return "instructor/delete";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idNumber,
                                @RequestParam(value = "urlOrigen", required = false, defaultValue = "/instructor/list") String urlOrigen,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:" + urlOrigen;

        // Comprobación de seguridad en el POST por si acaso
        List<?> actividades = impartsDao.getImpartsByInstructor(idNumber);
        if (actividades != null && !actividades.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeDenegado",
                    "No se puede eliminar a este instructor porque tiene actividades asignadas.");
            return "redirect:" + urlOrigen;
        }

        instructorDao.deleteInstructor(idNumber);
        redirectAttributes.addFlashAttribute("mensajeExito", "Instructor eliminado correctamente.");

        return "redirect:" + urlOrigen;
    }

    // ==========================================
    // VISTAS DE GESTIÓN GLOBAL (Listar y Añadir)
    // ==========================================

    @RequestMapping("/list")
    public String listInstructors(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("instructors", instructorDao.getInstructors());
        return "instructor/list";
    }

    @RequestMapping("/add")
    public String addInstructor(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";
        model.addAttribute("instructor", new Instructor());
        return "instructor/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("instructor") Instructor instructor,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        if (instructor.getIdNumber() != null && !instructor.getIdNumber().trim().isEmpty()) {
            Instructor existentePorDni = instructorDao.getInstructor(instructor.getIdNumber().trim().toUpperCase());
            if (existentePorDni != null) {
                bindingResult.rejectValue("idNumber", "duplicado", "Este DNI/NIE ya está registrado en el sistema.");
            }
        }
        instructorValidator.validate(instructor, bindingResult);
        if (bindingResult.hasErrors())
            return "instructor/add";

        instructor.setIdNumber(instructor.getIdNumber().trim().toUpperCase());
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(instructor.getUserPassword());
        instructor.setUserPassword(encryptedPassword);
        instructorDao.addInstructor(instructor);

        model.addAttribute("instructor", new Instructor());
        model.addAttribute("saveSuccess", true);
        return "instructor/add";
    }

    // ==========================================
    // VISTAS PRIVADAS / EDICIÓN PERFIL
    // ==========================================

    @RequestMapping(value = "/update/{idNumber}", method = RequestMethod.GET)
    public String editInstructor(Model model, @PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/update/" + idNumber);
            return "redirect:/login";
        }

        if (user.getTipoUsuario() != TipoUsuario.tecnico && user.getTipoUsuario() != TipoUsuario.instructor) {
            return "/auth/acceso-denegado";
        }

        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        if (user.getTipoUsuario() == TipoUsuario.instructor && !user.getIdNumber().equals(idNumber)) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("instructor", instructor);
        return "instructor/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) return "redirect:/login";

        if (user.getTipoUsuario() == TipoUsuario.instructor && !user.getIdNumber().equals(instructor.getIdNumber())) {
            return "/auth/acceso-denegado";
        }

        instructorValidator.validate(instructor, bindingResult);
        if (bindingResult.hasErrors())
            return "instructor/update";

        instructor.setIdNumber(instructor.getIdNumber().trim().toUpperCase());
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(instructor.getUserPassword());
        instructor.setUserPassword(encryptedPassword);
        instructorDao.updateInstructor(instructor);

        if (user.getIdNumber().equals(instructor.getIdNumber())) {
            user.setUserName(instructor.getUserName());
            session.setAttribute("user", user);
        }

        model.addAttribute("updateSuccess", true);
        return "instructor/update";
    }

    @RequestMapping("/panel")
    public String panel(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/panel");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.instructor) return "/auth/acceso-denegado";
        return "instructor/panel";
    }

    @RequestMapping(value = "/editar", method = RequestMethod.GET)
    public String editMiPerfil(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/editar");
            return "redirect:/login";
        }

        if (user.getTipoUsuario() != TipoUsuario.instructor) {
            return "/auth/acceso-denegado";
        }

        return "redirect:/instructor/update/" + user.getIdNumber();
    }
}
