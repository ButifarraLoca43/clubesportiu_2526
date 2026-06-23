package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ContractDao;
import es.uji.ei1027.oviaplication.model.Contract;
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
@RequestMapping("/contract")
public class ContractController {

    private ContractDao contractDao;

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    // ── Formulario AÑADIR ──────────────────────────────────────────
    @RequestMapping(value = "/addform/{idmatch}", method = RequestMethod.GET)
    public String addForm(@PathVariable int idmatch, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/contract/addform/" + idmatch);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser)
            return "/auth/acceso-denegado";

        Contract contract = new Contract();
        contract.setIdmatch(idmatch);
        model.addAttribute("contract", contract);
        return "contract/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAdd(@ModelAttribute("contract") Contract contract,
                             BindingResult bindingResult,
                             HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.OVIUser)
            return "/auth/acceso-denegado";

        if (contract.getUrl() == null || contract.getUrl().trim().isEmpty()) {
            bindingResult.rejectValue("url", "obligatorio", "La URL del contrato es obligatoria");
            return "contract/add";
        }

        contractDao.addContract(contract);
        model.addAttribute("saveSuccess", true);
        return "contract/add";
    }

    // ── Formulario EDITAR ──────────────────────────────────────────
    @RequestMapping(value = "/editform/{idmatch}", method = RequestMethod.GET)
    public String editForm(@PathVariable int idmatch, Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("nextUrl", "/contract/editform/" + idmatch);
            return "redirect:/login";
        }
        if (user.getTipoUsuario() != TipoUsuario.OVIUser)
            return "/auth/acceso-denegado";

        Contract contract = contractDao.getContract(idmatch);
        if (contract == null) return "redirect:/oviuser/listrequest";

        model.addAttribute("contract", contract);
        return "contract/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdate(@ModelAttribute("contract") Contract contract,
                                BindingResult bindingResult,
                                HttpSession session, Model model) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.OVIUser)
            return "/auth/acceso-denegado";

        if (contract.getUrl() == null || contract.getUrl().trim().isEmpty()) {
            bindingResult.rejectValue("url", "obligatorio", "La URL del contrato es obligatoria");
            return "contract/update";
        }

        contractDao.updateContract(contract);
        model.addAttribute("updateSuccess", true);
        return "contract/update";
    }
}