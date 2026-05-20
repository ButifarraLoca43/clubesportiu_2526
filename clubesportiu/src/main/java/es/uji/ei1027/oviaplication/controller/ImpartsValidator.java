package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.Imparts;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ImpartsValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Imparts.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Imparts imparts = (Imparts) target;

        if (imparts.getIdActivity() == null) {
            errors.rejectValue("idActivity", "obligatorio", "Debes seleccionar una actividad");
        }

        if (imparts.getIdInstructor() == null || imparts.getIdInstructor().trim().isEmpty()) {
            errors.rejectValue("idInstructor", "obligatorio", "Debes seleccionar un instructor");
        }
    }
}