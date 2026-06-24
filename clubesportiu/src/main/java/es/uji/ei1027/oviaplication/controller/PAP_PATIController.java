package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ContractDao;
import es.uji.ei1027.oviaplication.dao.InscriptionDao;
import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.Contract;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pap_pati")
public class PAP_PATIController {

    private PAP_PATIDao pap_patiDao;
    private MatchDao matchDao;
    private PapPatiValidator papPatiValidator;
    private ContractDao contractDao;
    private InscriptionDao inscriptionDao;

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
    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao=contractDao;
    }
    @Autowired
    public void setInscriptionDao(InscriptionDao inscriptionDao) {
        this.inscriptionDao=inscriptionDao;
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
    public String addPAP_PATI(Model model) {
        model.addAttribute("pap_pati", new PAP_PATI());
        return "pap_pati/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati,
                                   BindingResult bindingResult, Model model) {

        if (pap_pati.getIdNumber() != null && !pap_pati.getIdNumber().trim().isEmpty()) {
            PAP_PATI existentePorDni = pap_patiDao.getPAP_PATI(pap_pati.getIdNumber().trim().toUpperCase());
            if (existentePorDni != null) {
                bindingResult.rejectValue("idNumber", "duplicado", "Este DNI ya está registrado en el sistema.");
            }
        }

        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/add";

        pap_pati.setIdNumber(pap_pati.getIdNumber().trim().toUpperCase());
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        pap_pati.setUserPassword(passwordEncryptor.encryptPassword(pap_pati.getUserPassword()));
        pap_patiDao.addPAP_PATI(pap_pati);

        model.addAttribute("pap_pati", new PAP_PATI());
        model.addAttribute("saveSuccess", true);
        return "pap_pati/add";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable String idNumber, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/delete/" + idNumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) return "/auth/acceso-denegado";

        PAP_PATI pap_pati = pap_patiDao.getPAP_PATI(idNumber);
        if (pap_pati == null) return "redirect:../list";

        String referer = request.getHeader("Referer");
        String urlOrigen = (referer != null) ? referer : "/pap_pati/list";
        List<Map<String, Object>> asignacionesPendientes = pap_patiDao.getPendingMatchesForPap(idNumber);
        List<Map<String, Object>> asignacionesActivas = pap_patiDao.getActiveMatchesForPap(idNumber);
        boolean estaVinculado = (asignacionesPendientes != null && !asignacionesPendientes.isEmpty()) ||
                (asignacionesActivas != null && !asignacionesActivas.isEmpty());
        if (estaVinculado || inscriptionDao.hasInscriptionsOvi(idNumber)) {
            redirectAttributes.addFlashAttribute("mensajeDenegado",
                    "No se puede eliminar a este asistente porque tiene emparejamientos, contratos o está inscrito en actividades.");
            return "redirect:" + urlOrigen;
        }
        model.addAttribute("urlOrigen", urlOrigen);
        model.addAttribute("pap_pati", pap_pati);
        return "pap_pati/delete";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idNumber,
                                @RequestParam(value = "urlOrigen", required = false, defaultValue = "/pap_pati/list") String urlOrigen,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico)
            return "/auth/acceso-denegado";

        PAP_PATI pap_pati = pap_patiDao.getPAP_PATI(idNumber);
        if (pap_pati == null) return "redirect:" + urlOrigen;

        List<Map<String, Object>> asignacionesPendientes = pap_patiDao.getPendingMatchesForPap(idNumber);
        List<Map<String, Object>> asignacionesActivas = pap_patiDao.getActiveMatchesForPap(idNumber);
        boolean estaVinculado = (asignacionesPendientes != null && !asignacionesPendientes.isEmpty()) ||
                (asignacionesActivas != null && !asignacionesActivas.isEmpty());

        if (estaVinculado || inscriptionDao.hasInscriptionsOvi(idNumber)) {
            redirectAttributes.addFlashAttribute("mensajeDenegado",
                    "No se puede eliminar a este asistente porque tiene emparejamientos, contratos o está inscrito en actividades.");
            return "redirect:" + urlOrigen;
        }
        pap_patiDao.deletePAP_PATI(pap_pati);
        redirectAttributes.addFlashAttribute("mensajeExito", "Asistente eliminado correctamente.");
        if (urlOrigen.contains("/details/")) {
            return "redirect:/pap_pati/list";
        }
        return "redirect:" + urlOrigen;
    }

    @RequestMapping("/details/{idNumber}")
    public String detailsPAP_PATI(Model model, @PathVariable String idNumber, HttpSession session, HttpServletRequest request) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/pap_pati/details/" + idNumber);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        // 1. Calculamos inteligentemente la ruta de "Volver"
        String referer = request.getHeader("Referer");
        String urlVolver = (referer == null || referer.contains("/details/")) ? "/pap_pati/list" : referer;
        model.addAttribute("urlVolver", urlVolver);

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
    public String processUpdateSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati,
                                      BindingResult bindingResult, HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) return "/auth/acceso-denegado";

        if (user.getTipoUsuario() != TipoUsuario.tecnico) {
            if (user.getTipoUsuario() != TipoUsuario.PAP_PATI) return "/auth/acceso-denegado";
            PAP_PATI loggedPap = pap_patiDao.getPAP_PATIByUsername(user.getUserName());
            if (!loggedPap.getIdNumber().equals(pap_pati.getIdNumber())) return "/auth/acceso-denegado";
        }

        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/update";

        pap_pati.setIdNumber(pap_pati.getIdNumber().trim().toUpperCase());
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        pap_pati.setUserPassword(passwordEncryptor.encryptPassword(pap_pati.getUserPassword()));
        pap_patiDao.updatePAP_PATI(pap_pati);

        if (user.getTipoUsuario() == TipoUsuario.PAP_PATI) {
            user.setUserName(pap_pati.getUserName());
            session.setAttribute("user", user);
        }

        model.addAttribute("updateSuccess", true);
        return "pap_pati/update";
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

        for (Map<String, Object> asig : asignaciones) {
            System.out.println("CLAVES DEL MAPA: " + asig.keySet());
            System.out.println("VALORES: " + asig);
        }

        // Metemos el contrato directamente dentro de cada fila
        for (Map<String, Object> asig : asignaciones) {
            // Probamos las dos claves posibles según lo que devuelva tu SQL
            Object idmatch = asig.get("idmatch");
            if (idmatch == null) idmatch = asig.get("idnumber");
            if (idmatch != null) {
                Contract c = contractDao.getContract(((Number) idmatch).intValue());
                asig.put("contrato", c); // null si no existe, objeto si existe
            }
        }

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