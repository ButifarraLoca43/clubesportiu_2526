package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.OVIUser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OVIUserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return OVIUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OVIUser oviUser = (OVIUser) target;


        if (oviUser.getIdNumber() == null || oviUser.getIdNumber().trim().isEmpty()) {
            errors.rejectValue("idNumber", "obligatorio", "Hace falta introducir un valor");
        } else {
            // Si no es nulo, ya podemos procesar el String de forma segura
            String idNumber = oviUser.getIdNumber().trim().toUpperCase();
            if (!idNumber.matches("^[XYZ]?\\d{5,8}[A-Z]$")) {
                errors.rejectValue("idNumber", "DNI o NIE invalido", "Hace falta introducir un DNI o NIE validos");
            }
        }

        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "Hace falta introducir un nombre");
        }

        if (oviUser.getSurname() == null || oviUser.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatorio", "Hace falta introducir los apellidos");
        }

        if (oviUser.getDateBirth() == null) {
            errors.rejectValue("dateBirth", "obligatorio", "Hace falta una fecha de nacimiento");
        }

        if (oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "Hace falta introducir un correo");
        }

        if (oviUser.getPhoneNumber() == null || oviUser.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatorio", "Hace falta introducir un teléfono");
        }

        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty()) {
            errors.rejectValue("address", "obligatorio", "Hace falta introducir una dirección");
        }

        if (oviUser.getFuncDiversity() == null) {
            errors.rejectValue("funcDiversity", "obligatorio", "Hace falta indicar el tipo de diversidad funcional");
        }

        if (oviUser.getDependencyGrade() == null) {
            errors.rejectValue("dependencyGrade", "obligatorio", "Hace falta indicar el grado de dependencia");
        }

        if (oviUser.getUserName() == null || oviUser.getUserName().trim().isEmpty()) {
            errors.rejectValue("userName", "obligatorio", "Hace falta introducir un nombre de usuario");
        }

        if (oviUser.getUserPassword() == null || oviUser.getUserPassword().trim().isEmpty()) {
            errors.rejectValue("userPassword", "obligatorio", "Hace falta introducir una contraseña");
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
