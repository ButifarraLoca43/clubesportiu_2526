package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
    private InstructorDao instructorDao;
    private InstructorValidator instructorValidator;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao){ this.instructorDao = instructorDao; }
    @Autowired
    public void setInstructorValidator(InstructorValidator instructorValidator){ this.instructorValidator = instructorValidator; }

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
    public String processAddSubmit(@ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult, UserDetails user) {
        instructorValidator.validate(instructor, bindingResult);
        if (user == null) return "redirect:/login";
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";
        if (bindingResult.hasErrors())
            return "instructor/add";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(instructor.getUserPassword());
        instructor.setUserPassword(encryptedPassword);
        instructorDao.addInstructor(instructor);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/delete/" + idNumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";
        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        model.addAttribute("instructor",instructor);
        return "instructor/delete";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";
        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";
        instructorDao.deleteInstructor(idNumber);
        return "redirect:../list";
    }

    @RequestMapping(value = "/update/{idNumber}", method = RequestMethod.GET)
    public String editInstructor(Model model, @PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        // 1. Control de login básico
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/update/" + idNumber);
            return "redirect:/login";
        }

        // 2. Control de roles: Si no es técnico ni instructor, patada de inicio
        if (user.getTipoUsuario() != TipoUsuario.tecnico && user.getTipoUsuario() != TipoUsuario.instructor) {
            return "/auth/acceso-denegado";
        }

        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        // 3. Control de pertenencia: Si eres instructor, tu idNumber debe coincidir con el DNI de la URL
        if (user.getTipoUsuario() == TipoUsuario.instructor && !user.getIdNumber().equals(idNumber)) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("instructor", instructor);
        return "instructor/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");

        // Control de seguridad en el envío del formulario
        if (user == null) return "redirect:/login";

        // Si es un instructor intentando guardar los datos de otro DNI diferente al suyo... denegado.
        if (user.getTipoUsuario() == TipoUsuario.instructor && !user.getIdNumber().equals(instructor.getIdNumber())) {
            return "/auth/acceso-denegado";
        }

        instructorValidator.validate(instructor, bindingResult);
        if (bindingResult.hasErrors())
            return "instructor/update";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(instructor.getUserPassword());
        instructor.setUserPassword(encryptedPassword);
        instructorDao.updateInstructor(instructor);

        // Actualizamos proactivamente el userName de la sesión si es el propio usuario el que se edita
        if (user.getIdNumber().equals(instructor.getIdNumber())) {
            user.setUserName(instructor.getUserName());
            session.setAttribute("user", user);
        }

        // Redirección condicional según quién editó:
        if (user.getTipoUsuario() == TipoUsuario.tecnico) {
            return "redirect:/instructor/list"; // El técnico vuelve a la lista global
        } else {
            return "redirect:/instructor/panel"; // El instructor vuelve a su panel
        }
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

        // Control de login
        if (user == null) {
            session.setAttribute("nextUrl", "/instructor/editar");
            return "redirect:/login";
        }

        // Solo los instructores pueden usar este atajo
        if (user.getTipoUsuario() != TipoUsuario.instructor) {
            return "/auth/acceso-denegado";
        }

        // Redirige a la ruta de update oficial usando el DNI de la sesión
        return "redirect:/instructor/update/" + user.getIdNumber();
    }
}

