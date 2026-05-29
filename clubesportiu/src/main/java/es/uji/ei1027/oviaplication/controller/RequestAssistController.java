package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.RequestAssistDao;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.RequestAssist;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/requestAssist")
public class RequestAssistController {
    private RequestAssistDao requestAssistDao;
    private OVIUserDao oviUserDao;
    private RequestAssistValidator requestAssistValidator;

    @Autowired
    public void setRequestAssistDao(RequestAssistDao requestAssistDao){ this.requestAssistDao = requestAssistDao; }
    @Autowired
    public void setOVIUserDao(OVIUserDao oviUserDao){ this.oviUserDao = oviUserDao; }
    @Autowired
    public void setRequestAssistValidator(RequestAssistValidator requestAssistValidator){ this.requestAssistValidator = requestAssistValidator; }

    // ==========================================
    // VISTAS GLOBALES (Solo Técnico)
    // ==========================================

    @RequestMapping("/list")
    public String listRequestAssists(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/requestAssist/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("requestAssists", requestAssistDao.getRequestAssistsPorEstado("pendiente"));
        return "requestAssist/list";
    }

    // ==========================================
    // GESTIÓN DE SOLICITUDES (Técnico y OVIUser)
    // ==========================================

    @RequestMapping("/add")
    public String addRequestAssist(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/requestAssist/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        RequestAssist requestAssist = new RequestAssist();
        OVIUser oviUser = oviUserDao.getOVIUserByUsername(user.getUserName());

        if (oviUser != null) {
            requestAssist.setIduser(oviUser.getIdNumber()); // Mapeado a setIduser() de tu modelo
        }

        model.addAttribute("requestAssist", requestAssist);
        return "oviuser/requestassist";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        // CONTROL DE IDENTIDAD: Forzamos el iduser real del solicitante en el backend
        OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
        requestAssist.setIduser(loggedOvi.getIdNumber());

        requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "oviuser/requestassist";

        requestAssistDao.addRequestAssist(requestAssist);
        return "redirect:/oviuser/listrequest";
    }

    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable int idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/requestAssist/delete/" + idnumber);
            return "redirect:/login";
        }

        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null) return "redirect:../list";

        // CONTROL DE IDENTIDAD: El técnico puede ver el borrado. El OVI solo si es dueña de la solicitud.
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) return "/auth/acceso-denegado";

            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
            if (!requestAssist.getIduser().equals(loggedOvi.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
        }

        model.addAttribute("requestAssist", requestAssist);
        return "requestAssist/delete";
    }

    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null) return "redirect:../list";

        // CONTROL DE IDENTIDAD EN EL POST DE BORRADO
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) return "/auth/acceso-denegado";

            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
            if (!requestAssist.getIduser().equals(loggedOvi.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
        }

        requestAssistDao.deleteRequestAssist(requestAssist);

        if (user.getTipoUsuario() == TipoUsuario.tecnico) {
            return "redirect:../list";
        }
        return "redirect:/oviuser/listrequest";
    }

    @RequestMapping(value = "/update/{idnumber}", method = RequestMethod.GET)
    public String editRequestAssist(Model model, @PathVariable int idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/requestAssist/update/" + idnumber);
            return "redirect:/login";
        }

        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null) return "redirect:../list";

        // CONTROL DE IDENTIDAD: El técnico edita cualquiera, el OVI solo las suyas
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) return "/auth/acceso-denegado";

            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
            if (!requestAssist.getIduser().equals(loggedOvi.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
        }

        model.addAttribute("requestAssist", requestAssist);
        return "requestAssist/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // CORREGIDO: Buscamos en la BD usando getIdnumber() que coincide con tu modelo actual
        RequestAssist bdRequest = requestAssistDao.getRequestAssist(requestAssist.getIdnumber());

        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) return "/auth/acceso-denegado";

            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());

            // Si la solicitud original en la base de datos no pertenecía a este OVIUser, bloqueamos
            if (bdRequest != null && !bdRequest.getIduser().equals(loggedOvi.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
            // Forzamos el iduser para evitar manipulaciones maliciosas del formulario oculto
            requestAssist.setIduser(loggedOvi.getIdNumber());
        }

        requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "requestAssist/update";

        requestAssistDao.updateRequestAssist(requestAssist);

        if (user.getTipoUsuario() == TipoUsuario.tecnico) {
            return "redirect:/requestAssist/list";
        }
        return "redirect:/oviuser/panel";
    }
}