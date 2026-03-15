package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.MatchDao;
import es.uji.ei1027.oviaplication.model.Match;
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
    public String listMatches(Model model) {
        model.addAttribute("matches", matchDao.getMatches());
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

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editMatch(Model model, @PathVariable int id) {
        model.addAttribute("match", matchDao.getMatch(id));
        return "match/update";
    }

    @RequestMapping(value ="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("match") Match match, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "match/update";
        matchDao.updateMatch(match);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDelete(@PathVariable int id) {
        matchDao.deleteMatch(matchDao.getMatch(id));
        return "redirect:../list";
    }

}
