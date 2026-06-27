package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.RequestAssistDao;
import es.uji.ei1027.oviaplication.dao.TecnicoDao;
import es.uji.ei1027.oviaplication.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private TecnicoDao tecnicoDao;
    private RequestAssistDao requestAssistDao;
    private MatchDao matchDao;
    private OVIUserDao oviUserDao;

    @Autowired
    public void setTecnico(TecnicoDao tecnicoDao) {
        this.tecnicoDao=tecnicoDao;
    }
    @Autowired
    public void setRequestAssistDao(RequestAssistDao requestAssistDao) {
        this.requestAssistDao=requestAssistDao;
    }
    @Autowired
    public void setMatchDao(MatchDao matchDao) {
        this.matchDao=matchDao;
    }
    @Autowired
    public void setOVIUserDao(OVIUserDao oviUserDao) {
        this.oviUserDao = oviUserDao;
    }

    // ==========================================
    // PANEL PRINCIPAL
    // ==========================================

    @RequestMapping("/panel")
    public String panel(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/panel");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        return "tecnico/panel";
    }

    // ==========================================
    // GESTIÓN DE ASISTENTES PERSONALES (PAP)
    // ==========================================

    @RequestMapping("/papmanagement")
    public String papManagement(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/papmanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("pap_patis", tecnicoDao.getPAP_PATIsPorEstado("pendiente"));
        return "tecnico/papmanagement";
    }

    @RequestMapping("/aceptarPAP/{idNumber}")
    public String aceptarPAP(@PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/papmanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        tecnicoDao.updateEstadoPAP_PATI(idNumber, "aceptado");
        return "redirect:/tecnico/papmanagement";
    }

    @RequestMapping("/rechazarPAP/{idNumber}")
    public String rechazarPAP(@PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/papmanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        tecnicoDao.updateEstadoPAP_PATI(idNumber, "rechazado");
        return "redirect:/tecnico/papmanagement";
    }

    // ==========================================
    // GESTIÓN DE USUARIOS OVI
    // ==========================================

    @RequestMapping("/ovimanagement")
    public String oviManagement(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/ovimanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("ovis", tecnicoDao.getOVIUsersPorEstado("pendiente"));
        return "tecnico/ovimanagement";
    }

    @RequestMapping("/aceptarOVI/{idNumber}")
    public String aceptarOVI(@PathVariable String idNumber, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/ovimanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        tecnicoDao.updateEstadoOVIUser(idNumber, "aceptado");
        redirectAttributes.addFlashAttribute("feedbackOK",
                "El miembro ha sido aceptado correctamente.");
        return "redirect:/tecnico/ovimanagement";
    }

    @RequestMapping("/rechazarOVI/{idNumber}")
    public String rechazarOVI(@PathVariable String idNumber, HttpSession session, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/ovimanagement");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        tecnicoDao.updateEstadoOVIUser(idNumber, "rechazado");
        redirectAttributes.addFlashAttribute("feedbackKO",
                "La solicitud ha sido rechazada.");
        return "redirect:/tecnico/ovimanagement";
    }

    // ==========================================
    // GESTIÓN DE EMPAREJAMIENTOS (MATCHES)
    // ==========================================

    @RequestMapping("/match/create/{idnumber}")
    public String proponerMatch(Model model, @PathVariable String idnumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/match/create/" + idnumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        RequestAssist request = requestAssistDao.getRequestAssist(Integer.parseInt(idnumber));
        OVIUser oviUser = oviUserDao.getOVIUser(request.getIduser());

        Match match = new Match();
        match.setIdUser(request.getIduser());
        match.setDate(java.time.LocalDate.now());

        model.addAttribute("requestAssist", request); // <- Añadido para la vista
        model.addAttribute("oviUser", oviUser);       // <- Añadido para la vista
        model.addAttribute("papPatis", tecnicoDao.getPAP_PATIsPorEstado("aceptado"));
        model.addAttribute("requestId", idnumber);
        model.addAttribute("match", match);

        return "requestAssist/proponer";
    }

    @PostMapping("/match/asignar/{requestId}")
    public String asignarMatch(@PathVariable String requestId, RedirectAttributes redirectAttributes,
                               @RequestParam(value = "papIds", required = false) List<String> papIds,
                               HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";
        RequestAssist request = requestAssistDao.getRequestAssist(Integer.parseInt(requestId));

        if (papIds == null || papIds.isEmpty()) {
            model.addAttribute("errorSeleccion", true);
            model.addAttribute("papPatis", tecnicoDao.getPAP_PATIsPorEstado("aceptado"));
            model.addAttribute("requestId", requestId);
            OVIUser oviUser = oviUserDao.getOVIUser(request.getIduser());
            model.addAttribute("requestAssist", request);
            model.addAttribute("oviUser", oviUser);
            return "requestAssist/proponer";
        }

        for (String papId : papIds) {
            Match match = new Match();
            match.setIdUser(request.getIduser());
            match.setIdPAP(papId);
            match.setIdRequest(Integer.parseInt(requestId));
            match.setDate(java.time.LocalDate.now());
            matchDao.addMatch(match);
        }

        requestAssistDao.updateEstado(Integer.parseInt(requestId), "aceptado");
        redirectAttributes.addFlashAttribute("asignacionSuccess", true);
        return "redirect:/requestAssist/list";
    }

    @RequestMapping("/match/list")
    public String listMatches(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/tecnico/match/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        model.addAttribute("matches", tecnicoDao.getMatches());
        return "match/list";
    }
}