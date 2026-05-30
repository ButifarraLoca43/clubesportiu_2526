package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

@Component
public class PapPatiValidator implements Validator {

    @Autowired
    private PAP_PATIDao papPatiDao;

    @Autowired
    private OVIUserDao oviUserDao;

    @Autowired
    private InstructorDao instructorDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return PAP_PATI.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        PAP_PATI papPati = (PAP_PATI) obj;

        // --- DNI / idnumber (character varying(9) - NOT NULL) ---
        if (papPati.getIdNumber() == null || papPati.getIdNumber().trim().isEmpty()) {
            errors.rejectValue("idNumber", "obligatori", "El DNI es obligatorio");
        } else if (papPati.getIdNumber().length() > 9) {
            errors.rejectValue("idNumber", "longitud", "El DNI no puede superar los 9 caracteres");
        }

        // --- Nombre de Usuario / username (character varying(50)) ---
        if (papPati.getUserName() == null || papPati.getUserName().trim().isEmpty()) {
            errors.rejectValue("userName", "obligatori", "El nombre de usuario es obligatorio");
        } else if (papPati.getUserName().length() > 50) {
            errors.rejectValue("userName", "longitud", "El nombre de usuario no puede superar los 50 caracteres");
        } else {
            String nombreUsuario = papPati.getUserName();

            // Comprobación 1: Que no exista en la propia tabla de Instructores (de otro DNI)
            if (instructorDao.getInstructorByUserName(nombreUsuario) != null) {
                errors.rejectValue("userName", "duplicado", "Este nombre de usuario ya está registrado como Instructor.");
            }

            // Comprobación 2: Que no exista en la tabla de Usuarios OVI
            if (oviUserDao.getOVIUserByUsername(nombreUsuario) != null) {
                errors.rejectValue("userName", "error.oviuser", "Este nombre de usuario ya está registrado como usuario OVI.");
            }

            // Comprobación 3: Que no exista en la tabla de PAP/PATI
            if (papPatiDao.getPAP_PATIByUsername(nombreUsuario) != null) {
                errors.rejectValue("userName", "error.pappati", "Este nombre de usuario ya está registrado como PAP/PATI.");
            }
        }

        // --- Nombre / name (character varying(50) - NOT NULL) ---
        if (papPati.getName() == null || papPati.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatori", "El nombre es obligatorio");
        } else if (papPati.getName().length() > 50) {
            errors.rejectValue("name", "longitud", "El nombre no puede superar los 50 caracteres");
        }

        // --- Apellidos / surname (character varying(50) - NOT NULL) ---
        if (papPati.getSurname() == null || papPati.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatori", "Los apellidos son obligatorios");
        } else if (papPati.getSurname().length() > 50) {
            errors.rejectValue("surname", "longitud", "Los apellidos no pueden superar los 50 caracteres");
        }

        // --- Email / email (character varying(100) - NOT NULL) ---
        if (papPati.getEmail() == null || papPati.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatori", "El email es obligatorio");
        } else if (papPati.getEmail().length() > 100) {
            errors.rejectValue("email", "longitud", "El email no puede superar los 100 caracteres");
        } else if (!papPati.getEmail().matches(".*@.*\\..*")) {
            errors.rejectValue("email", "format", "El email debe tener un formato válido");
        }

        // --- Teléfono / phonenumber (character varying(15) - NOT NULL) ---
        // ¡Ojo! Estaba 'not null' en tu BD pero faltaba comprobarlo aquí
        if (papPati.getPhoneNumber() == null || papPati.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatori", "El teléfono es obligatorio");
        } else if (papPati.getPhoneNumber().length() > 15) {
            errors.rejectValue("phoneNumber", "longitud", "El teléfono no puede superar los 15 caracteres");
        }

        // --- Dirección / address (character varying(50) - Opcional) ---
        if (papPati.getAddress() != null && !papPati.getAddress().trim().isEmpty()) {
            if (papPati.getAddress().length() > 50) {
                errors.rejectValue("address", "longitud", "La dirección no puede superar los 50 caracteres");
            }
        }

        // --- Experiencia / experience (character varying(1000) - Opcional) ---
        if (papPati.getExperience() != null && !papPati.getExperience().trim().isEmpty()) {
            if (papPati.getExperience().length() > 1000) {
                errors.rejectValue("experience", "longitud", "La experiencia no puede superar los 1000 caracteres");
            }
        }

        // --- CV / curriculumvitae (character varying(1000) - Opcional) ---
        if (papPati.getCurriculumVitae() != null && !papPati.getCurriculumVitae().trim().isEmpty()) {
            if (papPati.getCurriculumVitae().length() > 1000) {
                errors.rejectValue("curriculumVitae", "longitud", "El enlace o texto del currículum no puede superar los 1000 caracteres");
            }
        }

        // --- Contraseña / userpassword (character varying(255)) ---
        if (papPati.getUserPassword() == null || papPati.getUserPassword().trim().isEmpty()) {
            errors.rejectValue("userPassword", "obligatori", "La contraseña es obligatoria");
        } else if (papPati.getUserPassword().length() < 6) {
            errors.rejectValue("userPassword", "minim", "La contraseña debe tener al menos 6 caracteres");
        } else if (papPati.getUserPassword().length() > 100) {
            // Límite prudente en texto plano para que al encriptarse no sature los 255 de la BD
            errors.rejectValue("userPassword", "longitud", "La contraseña es demasiado larga (máx 100 caracteres)");
        }

        // --- Fecha de nacimiento / datebirth (date - NOT NULL) ---
        if (papPati.getDateBirth() == null) {
            errors.rejectValue("dateBirth", "obligatori", "La fecha de nacimiento es obligatoria");
        } else {
            LocalDate avui = LocalDate.now();
            Period edat = Period.between(papPati.getDateBirth(), avui);

            if (edat.getYears() < 16) {
                errors.rejectValue("dateBirth", "edat", "Tiene que tener al menos 16 años para registrarse");
            } else if (edat.getYears() > 120) {
                errors.rejectValue("dateBirth", "pasat", "Introduce una fecha de nacimiento válida");
            }
        }
    }
}