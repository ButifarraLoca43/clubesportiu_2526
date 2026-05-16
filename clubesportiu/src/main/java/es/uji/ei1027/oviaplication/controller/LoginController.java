package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.UserDetailsDao;
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

        // Aquí añadir un validador

        UserDetails authenticatedUser = logInSvc.login(user.getUserName(), user.getUserPassword());
        if (authenticatedUser == null) {
            bindingResult.rejectValue("userPassword", "badpw", "Usuario o contraseña incorrectos");
            return "auth/login";
        }

        session.setAttribute("user", authenticatedUser);

        TipoUsuario tipoUsuario = authenticatedUser.getTipoUsuario();
        if (tipoUsuario == TipoUsuario.OVIUser){
            return "/oviuser/panel";
        } else if (tipoUsuario == TipoUsuario.PAP_PATI) {
            return "/pap_pati/panel";
        } else if (tipoUsuario == TipoUsuario.tecnico){
            return "/tecnico/panel";
        } else if (tipoUsuario == TipoUsuario.instructor){
            return "/instructor/panel";
        }

        // Si guardaste una 'nextUrl' en la sesión previamente.

        // Por defecto a la página principal.
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
