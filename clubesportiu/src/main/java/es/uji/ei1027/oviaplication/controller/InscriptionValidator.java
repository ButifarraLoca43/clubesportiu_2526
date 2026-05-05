package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.model.Inscription;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class InscriptionValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Inscription.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Inscription inscription = (Inscription) target;

        if (inscription.getIdactivity() == null) {
            errors.rejectValue("idactivity", "obligatorio", "Hace falta seleccionar la actividad a la que te inscribes");
        }

        boolean hasOvi = inscription.getIdovi() != null && !inscription.getIdovi().trim().isEmpty();
        boolean hasPap = inscription.getIdpap() != null && !inscription.getIdpap().trim().isEmpty();
        boolean hasExt = inscription.getIdext() != null && !inscription.getIdext().trim().isEmpty();

        int usuariosRellenos = 0;
        if (hasOvi) usuariosRellenos++;
        if (hasPap) usuariosRellenos++;
        if (hasExt) usuariosRellenos++;

        if (usuariosRellenos == 0) {
            errors.rejectValue("idovi", "obligatorio", "Debes indicar el usuario que se inscribe (OVI, PAP o EXT).");
        } else if (usuariosRellenos > 1) {
            errors.rejectValue("idovi", "exclusivo", "Una inscripción solo puede pertenecer a un usuario. Deja los otros dos campos en blanco.");
        }
    }
}