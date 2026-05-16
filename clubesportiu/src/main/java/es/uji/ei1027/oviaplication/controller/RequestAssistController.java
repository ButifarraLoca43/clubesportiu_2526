package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.RequestAssistDao;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.RequestAssist;
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

    @RequestMapping("/list")
    public String listRequestAssists(Model model) {
        model.addAttribute("requestAssists", requestAssistDao.getRequestAssistsPorEstado("pendiente"));
        return "requestAssist/list";
    }

    @RequestMapping("/add")
    public String addRequestAssist(Model model, HttpSession session) {
        RequestAssist requestAssist = new RequestAssist();
        UserDetails userDetails = (UserDetails) session.getAttribute("user");
        if (userDetails != null) {
            OVIUser oviUser = oviUserDao.getOVIUserByUsername(userDetails.getUserName());
            if (oviUser != null) {
                requestAssist.setIduser(oviUser.getIdNumber());
            }
        }
        model.addAttribute("requestAssist", requestAssist);
        return "oviuser/requestassist";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult) {
        requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "oviuser/requestassist";
        requestAssistDao.addRequestAssist(requestAssist);
        return "redirect:/oviuser/listrequest";
    }

    @RequestMapping(value = "/delete/idnumber", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable int idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";

        model.addAttribute("requestAssist",requestAssist);
        return "requestAssist/delete";
    }

    @RequestMapping(value = "/delete/{idnumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable int idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";
        requestAssistDao.deleteRequestAssist(requestAssist);
        return "redirect:../list";
    }

    @RequestMapping(value = "/update/{idnumber}", method = RequestMethod.GET)
    public String editRequestAssist(Model model, @PathVariable int idnumber) {
        RequestAssist requestAssist = requestAssistDao.getRequestAssist(idnumber);
        if (requestAssist == null)
            return "redirect:../list";
        model.addAttribute("requestAssist",requestAssist);
        return "requestAssist/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdareSubmit(@ModelAttribute("requestAssist") RequestAssist requestAssist, BindingResult bindingResult) {
        requestAssistValidator.validate(requestAssist, bindingResult);

        if (bindingResult.hasErrors())
            return "requestAssist/update";
        requestAssistDao.updateRequestAssist(requestAssist);
        return "oviuser/panel";
    }

}