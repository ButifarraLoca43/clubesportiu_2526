package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.TecnicoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private TecnicoDao tecnicoDao;
    @Autowired
    public void setTecnico(TecnicoDao tecnicoDao)
    {
        this.tecnicoDao=tecnicoDao;
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
}
