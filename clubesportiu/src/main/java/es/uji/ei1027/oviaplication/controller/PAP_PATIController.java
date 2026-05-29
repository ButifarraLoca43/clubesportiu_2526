package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pap_pati")
public class PAP_PATIController {

    private PAP_PATIDao pap_patiDao;
    private MatchDao matchDao;
    private PapPatiValidator papPatiValidator;

    @Autowired
    public void setPAP_PATIDao(PAP_PATIDao pap_patiDao) {
        this.pap_patiDao=pap_patiDao;
    }
    @Autowired
    public void setMatchDao(MatchDao matchDao) {
        this.matchDao=matchDao;
    }
    @Autowired
    public void setPapPatiValidator(PapPatiValidator papPatiValidator){
        this.papPatiValidator=papPatiValidator;
    }

    // ==========================================
    // VISTAS DE GESTIÓN GLOBAL (Solo Técnico)
    // ==========================================

    @RequestMapping("/list")
    public String listPAP_PATIs(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("pap_patis", pap_patiDao.getPAP_PATIs());
        return "pap_pati/list";
    }

    @RequestMapping("/add")
    public String addPAP_PATI(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("pap_pati", new PAP_PATI());
        return "pap_pati/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/add";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(pap_pati.getUserPassword());
        pap_pati.setUserPassword(encryptedPassword);
        pap_patiDao.addPAP_PATI(pap_pati);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idNumber}")
    public String processDelete(@PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        pap_patiDao.deletePAP_PATI(pap_patiDao.getPAP_PATI(idNumber));
        return "redirect:../list";
    }

    @RequestMapping("/details/{idNumber}")
    public String detailsPAP_PATI(Model model, @PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/details/" + idNumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("pap_pati", pap_patiDao.getPAP_PATI(idNumber));
        return "pap_pati/details";
    }

    // ==========================================
    // VISTAS PRIVADAS DEL PROPIO ASISTENTE (PAP_PATI / Técnico autorizado)
    // ==========================================

    @RequestMapping("/panel")
    public String panel(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/panel");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }
        return "pap_pati/panel";
    }

    @RequestMapping("editar")
    public String editar(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/editar");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        PAP_PATI pap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
        return "redirect:/pap_pati/update/" + pap.getIdNumber();
    }

    @RequestMapping(value = "/update/{idNumber}", method = RequestMethod.GET)
    public String editPAP_PATI(Model model, @PathVariable String idNumber, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/update/" + idNumber);
            return "redirect:/login";
        }

        // CONTROL DE IDENTIDAD REFORZADO: Solo técnicos o el propio dueño de la cuenta
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
                return "/auth/acceso-denegado";
            }
            PAP_PATI loggedPap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
            if (!loggedPap.getIdNumber().equals(idNumber)) {
                return "/auth/acceso-denegado"; // Intento de hackeo/acceso cruzado
            }
        }

        model.addAttribute("pap_pati", pap_patiDao.getPAP_PATI(idNumber));
        return "pap_pati/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "/auth/acceso-denegado";
        }

        // CONTROL DE IDENTIDAD EN EL ENVÍO DEL FORMULARIO
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
                return "/auth/acceso-denegado";
            }
            PAP_PATI loggedPap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
            if (!loggedPap.getIdNumber().equals(pap_pati.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
        }

        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/update";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(pap_pati.getUserPassword());
        pap_pati.setUserPassword(encryptedPassword);

        pap_patiDao.updatePAP_PATI(pap_pati);

        if (user.getTipoUsuario() == TipoUsuario.PAP_PATI) {
            user.setUserName(pap_pati.getUserName());
            session.setAttribute("user", user);
        }

        return "redirect:/pap_pati/panel";
    }

    @RequestMapping("/asignaciones")
    public String listAsignaciones(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/asignaciones");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        PAP_PATI pap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
        String idpap = pap.getIdNumber();

        List<Map<String, Object>> asignaciones = pap_patiDao.getPendingMatchesForPap(idpap);
        model.addAttribute("asignaciones", asignaciones);

        return "pap_pati/matchlist";
    }

    @RequestMapping("/activematch")
    public String activematch(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/activematch");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        PAP_PATI pap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
        String idpap = pap.getIdNumber();

        List<Map<String, Object>> asignaciones = pap_patiDao.getActiveMatchesForPap(idpap);
        model.addAttribute("asignaciones", asignaciones);

        return "pap_pati/activematch";
    }

    @RequestMapping(value = "/acceptMatch/{idRequest}/{idpap}")
    public String acceptMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/asignaciones");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        // CONTROL DE IDENTIDAD EN ACCIÓN DIRECTA: Un PAP solo puede aceptar asignaciones dirigidas a él
        PAP_PATI loggedPap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
        if (!loggedPap.getIdNumber().equals(idpap)) {
            return "/auth/acceso-denegado"; // El DNI de la URL no es el suyo
        }

        matchDao.updateEstado(idRequest, idpap, "aceptado_PAP");
        return "redirect:/pap_pati/activematch";
    }

    @RequestMapping(value = "/rejectMatch/{idRequest}/{idpap}")
    public String rejectMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/asignaciones");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        // CONTROL DE IDENTIDAD EN ACCIÓN DIRECTA: Un PAP solo puede rechazar sus propias asignaciones
        PAP_PATI loggedPap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
        if (!loggedPap.getIdNumber().equals(idpap)) {
            return "/auth/acceso-denegado";
        }

        matchDao.updateEstado(idRequest, idpap, "rechaza_PAP");
        return "redirect:/pap_pati/asignaciones";
    }
}