package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
    private InstructorDao instructorDao;
    private InstructorValidator instructorValidator;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao){ this.instructorDao = instructorDao; }
    @Autowired
    public void setInstructorValidator(InstructorValidator instructorValidator){ this.instructorValidator = instructorValidator; }

    @RequestMapping("/list")
    public String listInstructors(Model model) {
        model.addAttribute("instructors", instructorDao.getInstructors());
        return "instructor/list";
    }

    @RequestMapping("/add")
    public String addInstructor(Model model) {
        model.addAttribute("instructor", new Instructor());
        return "instructor/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {
        instructorValidator.validate(instructor, bindingResult);

        if (bindingResult.hasErrors())
            return "instructor/add";
        instructorDao.addInstructor(instructor);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.GET)
    public String deleteAsk(Model model, @PathVariable String idNumber) {
        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        model.addAttribute("instructor",instructor);
        return "instructor/delete";
    }

    @RequestMapping(value = "/delete/{idNumber}", method = RequestMethod.POST)
    public String processDelete(@PathVariable String idNumber) {
        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";
        instructorDao.deleteInstructor(idNumber);
        return "redirect:../list";
    }

    @RequestMapping(value = "/update/{idNumber}", method = RequestMethod.GET)
    public String editInstructor(Model model, @PathVariable String idNumber) {
        Instructor instructor = instructorDao.getInstructor(idNumber);
        if (instructor == null)
            return "redirect:../list";

        model.addAttribute("instructor",instructor);
        return "instructor/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("instructor") Instructor instructor,
            BindingResult bindingResult) {
        instructorValidator.validate(instructor, bindingResult);

        if (bindingResult.hasErrors())
            return "instructor/update";
        instructorDao.updateInstructor(instructor);
        return "redirect:list";
    }
}

