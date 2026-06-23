package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class InstructorValidator implements Validator {
    @Autowired
    private PAP_PATIDao papPatiDao;

    @Autowired
    private OVIUserDao oviUserDao;

    @Autowired
    private InstructorDao instructorDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return Instructor.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Instructor instructor = (Instructor) target;

        // --- DNI / NIE (character varying(9)) ---
        if (instructor.getIdNumber() == null || instructor.getIdNumber().trim().isEmpty()) {
            errors.rejectValue("idNumber", "obligatorio", "Introduce un DNI/NIE");
        } else if (instructor.getIdNumber().length() > 9) {
            errors.rejectValue("idNumber", "longitud", "El DNI/NIE no puede superar los 9 caracteres");
        }

        // --- NOMBRE (character varying(50)) ---
        if (instructor.getName() == null || instructor.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "Introduce un nombre");
        } else if (instructor.getName().length() > 50) {
            errors.rejectValue("name", "longitud", "El nombre no puede superar los 50 caracteres");
        }

        // --- APELLIDOS (character varying(50)) ---
        if (instructor.getSurname() == null || instructor.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatorio", "Introduce un apellido");
        } else if (instructor.getSurname().length() > 50) {
            errors.rejectValue("surname", "longitud", "Los apellidos no pueden superar los 50 caracteres");
        }

        // --- EMAIL (character varying(100)) ---
        if (instructor.getEmail() == null || instructor.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "Introduce un email válido");
        } else if (instructor.getEmail().length() > 100) {
            errors.rejectValue("email", "longitud", "El email no puede superar los 100 caracteres");
        } else if (!instructor.getEmail().matches(".*@.*\\..*")) {
            errors.rejectValue("email", "formato", "El email debe tener un formato válido");
        } else {
            Instructor existente = instructorDao.getInstructorByEmail(instructor.getEmail());
            if (existente != null && !existente.getIdNumber().equals(instructor.getIdNumber())) {
                errors.rejectValue("email", "duplicado", "El email ya existe");
            }
        }

        // --- TELÉFONO (character varying(9)) ---
        if (instructor.getPhoneNumber() == null || instructor.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatorio", "Introduce un número de teléfono");
        } else if (instructor.getPhoneNumber().length() > 9) {
            errors.rejectValue("phoneNumber", "longitud", "El teléfono no puede superar los 9 caracteres");
        }

        // --- DIRECCIÓN (character varying(150)) ---
        if (instructor.getAddress() == null || instructor.getAddress().trim().isEmpty()) {
            errors.rejectValue("address", "obligatorio", "Introduce una dirección de residencia");
        } else if (instructor.getAddress().length() > 150) {
            errors.rejectValue("address", "longitud", "La dirección no puede superar los 150 caracteres");
        }

        // --- FECHA DE NACIMIENTO ---
        if (instructor.getDateBirth() == null) {
            errors.rejectValue("dateBirth", "obligatorio", "Introduce una fecha de nacimiento");
        } else {
            if (instructor.getDateBirth().isAfter(LocalDate.now()))
                errors.rejectValue("dateBirth", "futuro", "La fecha de nacimiento no puede ser en el futuro");
            else if (instructor.getDateBirth().isAfter(LocalDate.now().minusYears(18)))
                errors.rejectValue("dateBirth", "menor", "El instructor debe ser mayor de edad");
            else if (instructor.getDateBirth().isBefore(LocalDate.now().minusYears(120)))
                errors.rejectValue("dateBirth", "pasado", "La fecha de nacimiento no puede ser tan antigua");
        }

        // --- FORMACIÓN (character varying(255)) ---
        if (instructor.getFormation() != null && !instructor.getFormation().trim().isEmpty()) {
            if (instructor.getFormation().length() > 255)
                errors.rejectValue("formation", "longitud", "La formación no puede tener más de 255 caracteres");
        }

        // --- EXPERIENCIA (character varying(255)) ---
        if (instructor.getExperience() != null && !instructor.getExperience().trim().isEmpty()) {
            if (instructor.getExperience().length() > 255)
                errors.rejectValue("experience", "longitud", "La experiencia no puede tener más de 255 caracteres");
        }

        // --- NOMBRE DE USUARIO (character varying(50)) ---
        if (instructor.getUserName() == null || instructor.getUserName().trim().isEmpty()) {
            errors.rejectValue("userName", "obligatorio", "Introduce un nombre de usuario");
        } else if (instructor.getUserName().length() > 50) {
            errors.rejectValue("userName", "longitud", "El nombre de usuario no puede superar los 50 caracteres");
        } else {
            String nombreUsuario = instructor.getUserName();

            // Comprobación 1: Que no exista en la propia tabla de Instructores (de otro DNI)
            Instructor instExistente = instructorDao.getInstructorByUserName(nombreUsuario);
            if (instExistente != null && !instExistente.getIdNumber().equals(instructor.getIdNumber())) {
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

        // --- CONTRASEÑA (character varying(255) tras encriptar) ---
        if (instructor.getUserPassword() == null || instructor.getUserPassword().trim().isEmpty()) {
            errors.rejectValue("userPassword", "obligatorio", "Introduce una contraseña");
        } else if (instructor.getUserPassword().length() > 100) {
            // Ponemos límite de 100 en texto plano para asegurarnos de que al encriptar quepa en los 255 de la BD
            errors.rejectValue("userPassword", "longitud", "La contraseña es demasiado larga (máx 100 caracteres)");
        }
    }
}