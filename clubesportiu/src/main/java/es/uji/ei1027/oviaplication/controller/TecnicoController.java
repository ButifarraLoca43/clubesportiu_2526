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

    @RequestMapping("/papmanagement")
    public String papManagement(Model model) {
        model.addAttribute("pap_patis", tecnicoDao.getPAP_PATIsPorEstado("pendiente"));
        return "tecnico/papmanagement";
    }

    @RequestMapping("/aceptar/{idNumber}")
    public String aceptar(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoPAP_PATI(idNumber, "aceptado");
        return "redirect:/tecnico/papmanagement";
    }

    @RequestMapping("/rechazar/{idNumber}")
    public String rechazar(@PathVariable String idNumber) {
        tecnicoDao.updateEstadoPAP_PATI(idNumber, "rechazado");
        return "redirect:/tecnico/papmanagement";
    }
}
