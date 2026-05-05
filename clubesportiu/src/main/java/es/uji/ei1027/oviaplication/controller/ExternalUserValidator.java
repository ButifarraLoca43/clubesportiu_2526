package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.ExternalUser;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ExternalUserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ExternalUser user = (ExternalUser) target;

        if (user.getIdnumber() == null || user.getIdnumber().trim().isEmpty()) {
            errors.rejectValue("idnumber", "obligatorio", "El DNI/NIE es obligatorio");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        }
        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatorio", "Los apellidos son obligatorios");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        }
        if (user.getPhonenumber() == null || user.getPhonenumber().trim().isEmpty()) {
            errors.rejectValue("phonenumber", "obligatorio", "El teléfono es obligatorio");
        }
    }
}