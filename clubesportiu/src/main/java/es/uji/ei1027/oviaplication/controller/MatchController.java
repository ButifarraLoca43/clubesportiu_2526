package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.ContractDao;
import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.model.Contract;
import es.uji.ei1027.oviaplication.model.Match;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/match")
public class MatchController {

    private MatchDao matchDao;

    private ContractDao contractDao;

    @Autowired
    public void setMatchDao(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    @Autowired
    public void setContractDao(ContractDao contractDao) {
        this.contractDao = contractDao;
    }

    @RequestMapping("/list")
    public String listMatches(Model model, HttpSession session) {
        model.addAttribute("matches", matchDao.getMatchesConNombres());        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }
        return "match/list";
    }

    @RequestMapping("/listAceptado")
    public String listMatchesAceptado(Model model, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }

        List<Map<String, Object>> matches = matchDao.getMatchesConNombresAceptados();

        // Cargamos contratos indexados por idnumber del match
        Map<String, Contract> contratos = new HashMap<>();
        for (Map<String, Object> asig : matches) {
            Object idmatch = asig.get("idnumber");
            if (idmatch != null) {
                int id = ((Number) idmatch).intValue(); // funciona con Integer y Long
                Contract c = contractDao.getContract(id);
                if (c != null) contratos.put(String.valueOf(id), c);
            }
        }
        model.addAttribute("contratos", contratos);
        model.addAttribute("matches", matches);
        return "match/listAceptado";
    }

    @RequestMapping(value = "/add")
    public String addMatch(Model model) {
        model.addAttribute("match", new Match());
        return "match/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("match") Match match, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "match/add";
        matchDao.addMatch(match);
        return "redirect:list";
    }

    @RequestMapping(value = "/update/{idnumber}", method = RequestMethod.GET)
    public String editMatch(Model model, @PathVariable String idnumber) {
        model.addAttribute("match", matchDao.getMatch(idnumber));
        return "match/update";
    }

    @RequestMapping(value ="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("match") Match match, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "match/update";
        matchDao.updateMatch(match);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idnumber}")
    public String processDelete(@PathVariable String idnumber) {
        matchDao.deleteMatch(matchDao.getMatch(idnumber));
        return "redirect:../list";
    }

}
