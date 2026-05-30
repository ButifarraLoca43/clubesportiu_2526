package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.model.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/oviuser")
public class OVIUserController {

    private OVIUserDao oviUserDao;
    private MatchDao matchDao;
    private OVIUserValidator oviUserValidator;

    @Autowired
    public void setOviUserDao(OVIUserDao oviUserDao){ this.oviUserDao = oviUserDao; }
    @Autowired
    public void setMatchDao(MatchDao matchDao){ this.matchDao = matchDao; }
    @Autowired
    public void setOviUserValidator(OVIUserValidator oviUserValidator){this.oviUserValidator = oviUserValidator;}

    // ==========================================
    // VISTAS DE GESTIÓN GLOBAL (Solo Técnico)
    // ==========================================

    @RequestMapping("/list")
    public String listOVIUsers(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/list");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("oviusers", oviUserDao.getOVIUsers());
        return "oviuser/list";
    }

    @RequestMapping(value="/add")
    public String addOVIUser(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/add");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("oviuser", new OVIUser());
        List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
        model.addAttribute("diversityList", listaDiversidad);
        return "oviuser/add";
    }

    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("oviuser") OVIUser user, BindingResult bindingResult, Model model, HttpSession session) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null || currentUser.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        oviUserValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            List<DiversityType> listaDiversidad = Arrays.asList(DiversityType.values());
            model.addAttribute("diversityList", listaDiversidad);
            return "oviuser/add";
        }
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        user.setUserPassword(passwordEncryptor.encryptPassword(user.getUserPassword()));
        oviUserDao.addOVIUser(user);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDelete(@PathVariable String id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        oviUserDao.deleteOVIUser(id);
        return "redirect:../list";
    }

    @RequestMapping("/details/{id}")
    public String detailsOVIUser(Model model, @PathVariable String id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/details/" + id);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        model.addAttribute("oviuser", oviUserDao.getOVIUser(id));
        return "oviuser/details";
    }

    // ==========================================
    // VISTAS PRIVADAS DEL PROPIO OVI (OVIUser / Técnico autorizado)
    // ==========================================

    @RequestMapping("/panel")
    public String panel(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/panel");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }
        return "oviuser/panel";
    }

    @RequestMapping("editar")
    public String editar(HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/editar");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        OVIUser oviUser = oviUserDao.getOVIUserByUsername(user.getUserName());
        return "redirect:/oviuser/update/" + oviUser.getIdNumber();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editOVIUser(Model model, @PathVariable String id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/update/" + id);
            return "redirect:/login";
        }

        // CONTROL DE IDENTIDAD REFORZADO: El técnico puede todo, el OVIUser solo a sí mismo
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
                return "/auth/acceso-denegado";
            }
            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
            if (!loggedOvi.getIdNumber().equals(id)) {
                return "/auth/acceso-denegado"; // Intento de editar a otro OVI
            }
        }

        model.addAttribute("oviuser", oviUserDao.getOVIUser(id));
        return "oviuser/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("oviuser") OVIUser oviUser, BindingResult bindingResult, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            return "/auth/acceso-denegado";
        }

        // CONTROL DE IDENTIDAD EN EL ENVÍO: Evita manipulación del formulario
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
                return "/auth/acceso-denegado";
            }
            OVIUser loggedOvi = oviUserDao.getOVIUserByUsername(user.getUserName());
            if (!loggedOvi.getIdNumber().equals(oviUser.getIdNumber())) {
                return "/auth/acceso-denegado";
            }
        }

        oviUserValidator.validate(oviUser, bindingResult);
        if (bindingResult.hasErrors())
            return "oviuser/update";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(oviUser.getUserPassword());
        oviUser.setUserPassword(encryptedPassword);

        oviUserDao.updateOVIUser(oviUser);

        if (user.getTipoUsuario() == TipoUsuario.OVIUser) {
            user.setUserName(oviUser.getUserName());
            session.setAttribute("user", user);
        }
        if (user.getTipoUsuario() == TipoUsuario.tecnico) {
            return "redirect:/tecnico/ovimanagment";
        }
        return "redirect:/oviuser/panel";
    }

    @RequestMapping("listrequest")
    public String listRequest(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/listrequest");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        OVIUser oviUser = oviUserDao.getOVIUserByUsername(user.getUserName());
        String id = oviUser.getIdNumber();

        List<Match> matches = matchDao.getMatchesUser(id);
        Set<Integer> matchedRequestIds = matches.stream()
                .map(Match::getIdRequest)
                .collect(Collectors.toSet());

        model.addAttribute("request", oviUserDao.getRequestAssistsUser(id));
        model.addAttribute("matchedRequestIds", matchedRequestIds);

        return "oviuser/matchlist";
    }

    @RequestMapping("/listAsignaciones/{idrequest}")
    public String listAssignedPAPs(Model model, @PathVariable("idrequest") int idRequest, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/listAsignaciones/" + idRequest);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        List<Map<String, Object>> paps = oviUserDao.getPAPsByRequest(idRequest);
        model.addAttribute("paps", paps);
        model.addAttribute("idRequest", idRequest);

        return "oviuser/assigned_paps";
    }

    @RequestMapping(value = "/acceptMatch/{idRequest}/{idpap}")
    public String acceptMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/listrequest");
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        matchDao.updateEstado(idRequest, idpap, "pendiente_PAP");
        matchDao.rejectOtherPAPs(idRequest);
        return "redirect:/oviuser/listrequest";
    }

    @RequestMapping(value = "/rejectMatch/{idRequest}/{idpap}")
    public String rejectMatch(@PathVariable("idRequest") int idRequest, @PathVariable("idpap") String idpap, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/oviuser/listAsignaciones/" + idRequest);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser) {
            return "/auth/acceso-denegado";
        }

        matchDao.updateEstado(idRequest, idpap, "rechaza_OVI");
        return "redirect:/oviuser/listAsignaciones/" + idRequest;
    }
}