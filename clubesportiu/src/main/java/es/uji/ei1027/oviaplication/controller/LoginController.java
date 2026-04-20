package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.UserDetailsDao;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
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
    private UserDetailsDao userDetailsDao;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "login"; // Devuelve la vista login.html [cite: 253]
    }

    @RequestMapping(value="/login", method= RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails user,
                             BindingResult bindingResult, HttpSession session) {

        // Aquí añadir un validador

        UserDetails authenticatedUser = userDetailsDao.loadUserByUsername(user.getUserName(), user.getUserPassword());
        if (authenticatedUser == null) {
            bindingResult.rejectValue("userPassword", "badpw", "Usuario o contraseña incorrectos");
            return "login"; // Vuelve al formulario si falla [cite: 258]
        }

        session.setAttribute("user", authenticatedUser);

        TipoUsuario tipoUsuario = authenticatedUser.getTipoUsuario();
        if (tipoUsuario == TipoUsuario.OVIUser){
            return "redirect:/oviuser";
        } else if (tipoUsuario == TipoUsuario.PAP_PATI) {
            return "redirect:/pap_pati";
        } else if (tipoUsuario == TipoUsuario.tecnico){
            return "redirect:/tecnico";
        }

        // Si guardaste una 'nextUrl' en la sesión previamente.

        // Por defecto a la página principal.
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalida la sesión actual [cite: 188, 261]
        return "redirect:/";
    }

    @RequestMapping(value="/lista-userdetails", method = RequestMethod.GET)
    public String probarLista(Model model) {

        model.addAttribute("lista", userDetailsDao.listAllUsers());
        return "auth/lista";
    }
}
