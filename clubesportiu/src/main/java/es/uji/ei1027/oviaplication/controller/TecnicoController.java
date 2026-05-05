package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.dao.RequestAssistDao;
import es.uji.ei1027.oviaplication.dao.TecnicoDao;
import es.uji.ei1027.oviaplication.model.Match;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import es.uji.ei1027.oviaplication.model.RequestAssist;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private TecnicoDao tecnicoDao;
    private RequestAssistDao requestAssistDao;
    private PAP_PATIDao papPatiDao;
    @Autowired
    public void setTecnico(TecnicoDao tecnicoDao)
    {
        this.tecnicoDao=tecnicoDao;
    }
    @Autowired
    public void setRequestAssistDao(RequestAssistDao requestAssistDao)
    {
        this.requestAssistDao=requestAssistDao;
    }
    @Autowired
    public void setPAP_PATIDao(PAP_PATIDao papPatiDao)
    {
        this.papPatiDao=papPatiDao;
    }

    @RequestMapping("/panel")
    public String panel() {
        return "tecnico/panel";
    }


    //PAP_PATI

    @RequestMapping("/papmanagement")
    public String papManagement(Model model) {
        model.addAttribute("pap_patis", tecnicoDao.getPAP_PATIsPorEstado("pendiente"));
        return "tecnico/papmanagement";
    }

    @RequestMapping("/aceptarPAP/{idNumber}")
    public String aceptarPAP(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoPAP_PATI(idNumber, "aceptado");
        return "redirect:/tecnico/papmanagement";
    }

    @RequestMapping("/rechazarPAP/{idNumber}")
    public String rechazarPAP(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoPAP_PATI(idNumber, "rechazado");
        return "redirect:/tecnico/papmanagement";
    }


    //OVIUser

    @RequestMapping("/ovimanagement")
    public String oviManagement(Model model) {
        model.addAttribute("ovis", tecnicoDao.getOVIUsersPorEstado("pendiente"));
        return "tecnico/ovimanagement";
    }

    @RequestMapping("/aceptarOVI/{idNumber}")
    public String aceptarOVI(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoOVIUser(idNumber, "aceptado");
        return "redirect:/tecnico/ovimanagement";
    }

    @RequestMapping("/rechazarOVI/{idNumber}")
    public String rechazarOVI(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoOVIUser(idNumber, "rechazado");
        return "redirect:/tecnico/ovimanagement";
    }

    @RequestMapping("/match/create/{idnumber}")
    public String proponerMatch(Model model, @PathVariable String idnumber) {
        RequestAssist request = requestAssistDao.getRequestAssist(Integer.parseInt(idnumber));

        Match match = new Match();
        match.setIdUser(request.getIduser());
        match.setDate(java.time.LocalDate.now());


        model.addAttribute("papPatis", tecnicoDao.getPAP_PATIsPorEstado("aceptado"));
        model.addAttribute("requestId", idnumber);
        model.addAttribute("match", match);

        return "requestAssist/proponer";

    }


}
