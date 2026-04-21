package es.uji.ei1027.oviaplication.controller;


import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/pap_pati")
public class PAP_PATIController
{
    private PAP_PATIDao pap_patiDao;

    @Autowired
    public void setPAP_PATIDao(PAP_PATIDao pap_patiDao)
    {
        this.pap_patiDao=pap_patiDao;
    }

    @RequestMapping("/list")
    public String listPAP_PATIs(Model model)
    {
        model.addAttribute("pap_patis", pap_patiDao.getPAP_PATIs());
        return "pap_pati/list";
    }

    @RequestMapping("/add")
    public String addPAP_PATI(Model model) {
        model.addAttribute("pap_pati", new PAP_PATI());
        return "pap_pati/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati, BindingResult bindingResult) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/add";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(pap_pati.getUserPassword());
        pap_pati.setUserPassword(encryptedPassword);
        pap_patiDao.addPAP_PATI(pap_pati);
        return "redirect:list";
    }

    @RequestMapping(value = "/update/{idNumber}", method = RequestMethod.GET)
    public String editPAP_PATI(Model model, @PathVariable String idNumber) {
        model.addAttribute("pap_pati", pap_patiDao.getPAP_PATI(idNumber));
        return "pap_pati/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("pap_pati") PAP_PATI pap_pati, BindingResult bindingResult) {
        PapPatiValidator papPatiValidator = new PapPatiValidator();
        papPatiValidator.validate(pap_pati, bindingResult);
        if (bindingResult.hasErrors())
            return "pap_pati/update";

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(pap_pati.getUserPassword());
        pap_pati.setUserPassword(encryptedPassword);
        pap_patiDao.updatePAP_PATI(pap_pati);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idNumber}")
    public String processDelete(@PathVariable String idNumber) {
        pap_patiDao.deletePAP_PATI(pap_patiDao.getPAP_PATI(idNumber));
        return "redirect:../list";
    }

}