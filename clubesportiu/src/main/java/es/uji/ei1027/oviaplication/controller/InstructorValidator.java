package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class InstructorValidator implements Validator {
    @Autowired
    private InstructorDao instructorDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return Instructor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Instructor instructor = (Instructor) target;

        //Comprobar no nulo y no "  "
        if (instructor.getIdNumber() == null || instructor.getIdNumber().trim().equals(""))
            errors.rejectValue("idNumber", "obligatorio", "Introduce un DNI/NIE");


        if (instructor.getName() == null || instructor.getName().trim().equals(""))
            errors.rejectValue("name", "obligatorio", "Introduce un nombre");


        if (instructor.getSurname() == null || instructor.getSurname().trim().equals(""))
            errors.rejectValue("surname", "obligatorio", "Introduce un apellido");


        if (instructor.getEmail() == null || instructor.getEmail().trim().equals(""))
            errors.rejectValue("email", "obligatorio", "Introduce un email válido");
        else if (!instructor.getEmail().matches(".*@.*\\..*"))
            errors.rejectValue("email", "formato", "El email debe tener un formato válido");
        else
        {
            Instructor existente = instructorDao.getInstructorByEmail(instructor.getEmail());
            if (existente != null && !existente.getIdNumber().equals(instructor.getIdNumber())) {
                errors.rejectValue("email", "duplicado", "El email ya existe");
            }
        }

        if (instructor.getPhoneNumber() == null || instructor.getPhoneNumber().trim().equals(""))
            errors.rejectValue("phoneNumber", "obligatorio", "Introduce un número de teléfono");


        if (instructor.getAddress() == null || instructor.getAddress().trim().equals(""))
            errors.rejectValue("address", "obligatorio", "Introduce una dirección de residencia");


        //fecha no muy pasada ni futura, mayor de 18
        if (instructor.getDateBirth() == null)
            errors.rejectValue("dateBirth", "obligatorio", "Introduce una fecha de nacimiento");
        else
        {
            if(instructor.getDateBirth().isAfter(LocalDate.now()))
                errors.rejectValue("dateBirth", "futuro", "La fecha de nacimiento no puede ser en el futuro");
            else if(instructor.getDateBirth().isAfter(LocalDate.now().minusYears(18)))
                errors.rejectValue("dateBirth", "menor", "El instructor debe ser mayor de edad");
            else if(instructor.getDateBirth().isBefore(LocalDate.now().minusYears(120)))
                errors.rejectValue("dateBirth", "pasado", "La fecha de nacimiento no puede ser tan antigua");
        }


        if(instructor.getFormation() != null && !instructor.getFormation().trim().equals(""))
        {
            if(instructor.getFormation().length() > 255)
                errors.rejectValue("formation", "longitud", "La formación no puede tener más de 255 caracteres");
        }


        if(instructor.getExperience() != null && !instructor.getExperience().trim().equals(""))
        {
            if(instructor.getExperience().length() > 255)
                errors.rejectValue("experience", "longitud", "La experiencia no puede tener más de 255 caracteres");
        }


        if (instructor.getUserName() == null || instructor.getUserName().trim().equals(""))
            errors.rejectValue("userName", "obligatorio", "Introduce un nombre de usuario");
        else
        {
            Instructor existente = instructorDao.getInstructorByUserName(instructor.getUserName());
            if (existente != null && !existente.getIdNumber().equals(instructor.getIdNumber())) {
                errors.rejectValue("userName", "duplicado", "El nombre de usuario ya existe");
            }
        }


        //contraseña no nula ni muy corta
        if (instructor.getUserPassword() == null || instructor.getUserPassword().trim().equals(""))
            errors.rejectValue("userPassword", "obligatorio", "Introduce una contraseña");
        else if (instructor.getUserPassword().length() < 6)
            errors.rejectValue("userPassword", "longitud", "La contraseña debe tener al menos 6 caracteres");



    }

}
