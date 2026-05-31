package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.UserDetailsDao;
import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import es.uji.ei1027.oviaplication.services.LogInSvc;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private LogInSvc logInSvc;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "auth/login";
    }

    @RequestMapping(value="/login", method= RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails user,
                             BindingResult bindingResult, HttpSession session) {

        // 1. Autenticar credenciales básicas (Usuario y Contraseña)
        UserDetails authenticatedUser = logInSvc.login(user.getUserName(), user.getUserPassword());
        if (authenticatedUser == null) {
            bindingResult.rejectValue("userPassword", "badpw", "Usuario o contraseña incorrectos");
            return "auth/login";
        }

        // 2. NUEVA COMPROBACIÓN: Control de estado (Pendiente / Rechazado)
        if (authenticatedUser.getEstado() != null) {
            if (authenticatedUser.getEstado() == Estado.pendiente) {
                bindingResult.rejectValue("userPassword", "accountPending",
                        "Tu solicitud de acceso todavía está pendiente de revisión por un administrador.");
                return "auth/login";
            }

            if (authenticatedUser.getEstado() == Estado.rechazado) {
                bindingResult.rejectValue("userPassword", "accountRejected",
                        "Tu acceso al sistema ha sido rechazado. Si crees que es un error, contacta con soporte.");
                return "auth/login";
            }
        }

        // 3. Si el estado es 'aceptado' (o no tiene estado, como técnicos), se inicia sesión
        session.setAttribute("user", authenticatedUser);

        Object objNextUrl = session.getAttribute("nextUrl");
        if (objNextUrl != null){
            session.removeAttribute("nextUrl");
            String nextUrl = objNextUrl.toString();
            return "redirect:" + nextUrl;
        }

        TipoUsuario tipoUsuario = authenticatedUser.getTipoUsuario();
        if (tipoUsuario == TipoUsuario.OVIUser){
            return "redirect:/oviuser/panel";
        } else if (tipoUsuario == TipoUsuario.PAP_PATI) {
            return "redirect:/pap_pati/panel";
        } else if (tipoUsuario == TipoUsuario.tecnico){
            return "redirect:/tecnico/panel";
        } else if (tipoUsuario == TipoUsuario.instructor){
            return "redirect:/instructor/panel";
        }

        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
