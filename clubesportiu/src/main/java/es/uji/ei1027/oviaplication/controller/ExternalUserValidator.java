package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.ExternalUser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ExternalUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ExternalUser user = (ExternalUser) target;

        // --- DNI / NIE (idnumber) ---
        if (user.getIdnumber() == null || user.getIdnumber().trim().isEmpty()) {
            errors.rejectValue("idnumber", "obligatorio", "El DNI/NIE es obligatorio");
        } else if (user.getIdnumber().trim().length() > 9) {
            errors.rejectValue("idnumber", "longitud", "El DNI/NIE no puede superar los 9 caracteres");
        }

        // --- Nombre ---
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "El nombre es obligatorio");
        } else if (user.getName().length() > 50) {
            errors.rejectValue("name", "longitud", "El nombre no puede superar los 50 caracteres");
        }

        // --- Apellidos ---
        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatorio", "Los apellidos son obligatorios");
        } else if (user.getSurname().length() > 50) {
            errors.rejectValue("surname", "longitud", "Los apellidos no pueden superar los 50 caracteres");
        }

        // --- Email ---
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (user.getEmail().length() > 100) {
            errors.rejectValue("email", "longitud", "El email no puede superar los 100 caracteres");
        } else if (!user.getEmail().matches(".*@.*\\..*")) {
            errors.rejectValue("email", "formato", "El email debe tener un formato válido");
        }

        // --- Teléfono ---
        if (user.getPhonenumber() == null || user.getPhonenumber().trim().isEmpty()) {
            errors.rejectValue("phonenumber", "obligatorio", "El teléfono es obligatorio");
        } else if (user.getPhonenumber().length() > 9) {
            errors.rejectValue("phonenumber", "longitud", "El teléfono no puede superar los 9 caracteres");
        }
    }
}