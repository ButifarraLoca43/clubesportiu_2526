package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.RequestAssist;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RequestAssistValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz)
    {
        return RequestAssist.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestAssist r = (RequestAssist) target;

        if (r.getIduser() == null || r.getIduser().trim().isEmpty()) {
            errors.rejectValue("iduser", "obligatorio", "El DNI/NIE del usuario es obligatorio");
        }

        if (r.getDate() == null) {
            errors.rejectValue("date", "obligatorio", "La fecha es obligatoria");
        }

        if (r.getRequiredsupport() == null || r.getRequiredsupport().trim().isEmpty()) {
            errors.rejectValue("requiredsupport", "obligatorio", "El tipo de apoyo requerido es obligatorio");
        } else if (r.getRequiredsupport().length() > 500) {
            errors.rejectValue("requiredsupport", "demasiadoLargo", "Máximo 500 caracteres");
        }

        if (r.getDescription() == null || r.getDescription().trim().isEmpty()) {
            errors.rejectValue("description", "obligatorio", "La descripción es obligatoria");
        } else if (r.getDescription().length() > 1000) {
            errors.rejectValue("description", "demasiadoLargo", "Máximo 1000 caracteres");
        }

        if (r.getRequirements() == null || r.getRequirements().trim().isEmpty()) {
            errors.rejectValue("requirements", "obligatorio", "Los requisitos son obligatorios");
        } else if (r.getRequirements().length() > 1000) {
            errors.rejectValue("requirements", "demasiadoLargo", "Máximo 1000 caracteres");
        }

        if (r.getLifeproject() == null || r.getLifeproject().trim().isEmpty()) {
            errors.rejectValue("lifeproject", "obligatorio", "El proyecto de vida es obligatorio");
        } else if (r.getLifeproject().length() > 1000) {
            errors.rejectValue("lifeproject", "demasiadoLargo", "Máximo 1000 caracteres");
        }
    }
}
