package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.MatchDao;
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

@Controller
@RequestMapping("/match")
public class MatchController {

    private MatchDao matchDao;

    @Autowired
    public void setMatchDao(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    @RequestMapping("/list")
    public String listMatches(Model model, HttpSession session) {
        model.addAttribute("matches", matchDao.getMatches());
        UserDetails user = (UserDetails) session.getAttribute("user");
        if (user == null || user.getTipoUsuario() != TipoUsuario.tecnico) {
            return "/auth/acceso-denegado";
        }
        return "match/list";
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
