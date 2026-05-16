package es.uji.ei1027.oviaplication.controller; // Ajusta el paquete al tuyo

import es.uji.ei1027.oviaplication.model.PAP_PATI; // Ajusta el paquete al tuyo
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

@Component
public class PapPatiValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PAP_PATI.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        PAP_PATI papPati = (PAP_PATI) obj;

        // 1. Validar campos obligatorios (los que en la BD son NOT NULL)
        if (papPati.getIdNumber() == null || papPati.getIdNumber().trim().isEmpty()) {
            errors.rejectValue("idNumber", "obligatori", "El DNI es obligatorio");
        }

        if (papPati.getUserName() == null || papPati.getUserName().trim().isEmpty()) {
            errors.rejectValue("userName", "obligatori", "El nombre de usuario es obligatorio");
        }

        if (papPati.getName() == null || papPati.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatori", "El nombre es obligatorio");
        }

        if (papPati.getSurname() == null || papPati.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatori", "Los apellidos son obligatorios");
        }

        if (papPati.getEmail() == null || papPati.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "El email es obligatorio");
        }

        if (papPati.getUserPassword() == null || papPati.getUserPassword().trim().isEmpty()) {
            errors.rejectValue("userPassword", "obligatori", "La contraseña es obligatoria");
        }

        // 2. Validar la fecha de nacimiento (Regla de los 16 años)
        if (papPati.getDateBirth() == null) {
            errors.rejectValue("dateBirth", "obligatori", "La fecha de nacimiento es obligatoria");
        } else {
            // Comprobamos si tiene al menos 16 años
            LocalDate avui = LocalDate.now();
            Period edat = Period.between(papPati.getDateBirth(), avui);

            if (edat.getYears() < 16) {
                // Si la diferencia es menor a 16 años, lanzamos el error
                errors.rejectValue("dateBirth", "edat", "Tiene que tener al menos 16 años para registrare");
            }
        }
    }
}
