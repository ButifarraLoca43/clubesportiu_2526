package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.RequestAssistDao;
import es.uji.ei1027.oviaplication.model.RequestAssist;
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
    //private RequestAssistValidator requestAssistValidator;

    @Autowired
    public void setRequestAssistDao(RequestAssistDao requestAssistDao){ this.requestAssistDao = requestAssistDao; }
    //@Autowired
    //public void setRequestAssistValidator(RequestAssistValidator requestAssistValidator){ this.requestAssistValidator = requestAssistValidator; }

    @RequestMapping("/list")
    public String listRequestAssists(Model model) {
        model.addAttribute("requestAssists", requestAssistDao.getRequestAssists());
        return "requestAssist/list";
    }

    @RequestMapping("/add")
    public String addRequestAssist(Model model) {
        model.addAttribute("requestAssist", new RequestAssist());
        return "requestAssist/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String processAddSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult) {
        //requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "requestAssist/add";
        requestAssistDao.addRequestAssist(requestAssist);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/idnumber", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable String idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";

        model.addAttribute("requestAssist",requestAssist);
        return "requestAssist/delete";
    }

    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";
        requestAssistDao.deleteRequestAssist(requestAssist);
        return "redirect:../list";
    }

    @RequestMapping(value = "/update/{idnumber}", method = RequestMethod.GET)
    public String editRequestAssist(Model model, @PathVariable String idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";
        model.addAttribute("requestAssist",requestAssist);
        return "requestAssist/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdareSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult) {
        //requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "requestAssist/update";
        requestAssistDao.updateRequestAssist(requestAssist);
        return "redirect:list";
    }
}


