package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import es.uji.ei1027.oviaplication.services.LogInSvc;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Asegúrate de importar Model
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
                             BindingResult bindingResult, HttpSession session, Model model) {

        // 1. Autenticar credenciales básicas (Usuario y Contraseña)
        UserDetails authenticatedUser = logInSvc.login(user.getUserName(), user.getUserPassword());
        if (authenticatedUser == null) {
            bindingResult.rejectValue("userPassword", "badpw", "Usuario o contraseña incorrectos");
            return "auth/login";
        }

        if (authenticatedUser.getEstado() != null) {
            if (authenticatedUser.getEstado() == Estado.pendiente) {
                model.addAttribute("modalMessage", "Tu solicitud de acceso todavía está pendiente de revisión por uno de nuestros técnicos.");
                return "auth/login";
            }

            if (authenticatedUser.getEstado() == Estado.rechazado) {
                model.addAttribute("modalMessage", "Tu acceso al sistema ha sido rechazado. Si crees que es un error, contacta con soporte.");
                return "auth/login";
            }
        }

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
