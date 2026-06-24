package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.model.Instructor;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OVIUserValidator implements Validator {

    // Inyectamos los DAOs para poder comprobar nombres de usuario duplicados
    private OVIUserDao oviUserDao;
    private PAP_PATIDao papPatiDao;
    private InstructorDao instructorDao;

    @Autowired
    public void setDaos(OVIUserDao oviUserDao, PAP_PATIDao papPatiDao, InstructorDao instructorDao) {
        this.oviUserDao = oviUserDao;
        this.papPatiDao = papPatiDao;
        this.instructorDao = instructorDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return OVIUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OVIUser oviUser = (OVIUser) target;

        // --- DNI / IDNUMBER (character varying(9)) ---
        if (oviUser.getIdNumber() == null || oviUser.getIdNumber().trim().isEmpty()) {
            errors.rejectValue("idNumber", "obligatorio", "Hace falta introducir un DNI o NIE");
        } else if (oviUser.getIdNumber().length() > 9) {
            errors.rejectValue("idNumber", "longitud", "El DNI/NIE no puede superar los 9 caracteres");
        } else {
            String idNumber = oviUser.getIdNumber().trim().toUpperCase();
            if (!idNumber.matches("^[XYZ]?\\d{5,8}[A-Z]$")) {
                errors.rejectValue("idNumber", "invalido", "Formato de DNI o NIE inválido");
            }
        }

        // --- NAME (character varying(50)) ---
        if (oviUser.getName() == null || oviUser.getName().trim().isEmpty()) {
            errors.rejectValue("name", "obligatorio", "Hace falta introducir un nombre");
        } else if (oviUser.getName().length() > 50) {
            errors.rejectValue("name", "longitud", "El nombre no puede superar los 50 caracteres");
        }

        // --- SURNAME (character varying(50)) ---
        if (oviUser.getSurname() == null || oviUser.getSurname().trim().isEmpty()) {
            errors.rejectValue("surname", "obligatorio", "Hace falta introducir los apellidos");
        } else if (oviUser.getSurname().length() > 50) {
            errors.rejectValue("surname", "longitud", "Los apellidos no pueden superar los 50 caracteres");
        }

        // --- DATEBIRTH ---
        if (oviUser.getDateBirth() == null) {
            errors.rejectValue("dateBirth", "obligatorio", "Hace falta una fecha de nacimiento");
        }

        // --- EMAIL (character varying(100) - UNIQUE) ---
        if (oviUser.getEmail() == null || oviUser.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "Hace falta introducir un correo electrónico");
        } else if (oviUser.getEmail().length() > 100) {
            errors.rejectValue("email", "longitud", "El correo no puede superar los 100 caracteres");
        } else {
            // Comprobación de duplicado
            OVIUser existentePorEmail = oviUserDao.getOVIUserByEmail(oviUser.getEmail().trim());
            if (existentePorEmail != null && !existentePorEmail.getIdNumber().equals(oviUser.getIdNumber())) {
                errors.rejectValue("email", "duplicado", "Este correo electrónico ya está registrado en el sistema.");
            }
        }

        // --- PHONENUMBER (character varying(9)) ---
        // En BBDD es nullable, pero si quieres que sea obligatorio en el registro:
        if (oviUser.getPhoneNumber() == null || oviUser.getPhoneNumber().trim().isEmpty()) {
            errors.rejectValue("phoneNumber", "obligatorio", "Hace falta introducir un teléfono");
        } else if (oviUser.getPhoneNumber().length() > 9) {
            errors.rejectValue("phoneNumber", "longitud", "El teléfono no puede superar los 9 caracteres");
        }

        // --- ADDRESS (character varying(150)) ---
        // En BBDD es nullable, pero si quieres que sea obligatorio:
        if (oviUser.getAddress() == null || oviUser.getAddress().trim().isEmpty()) {
            errors.rejectValue("address", "obligatorio", "Hace falta introducir una dirección");
        } else if (oviUser.getAddress().length() > 150) {
            errors.rejectValue("address", "longitud", "La dirección no puede superar los 150 caracteres");
        }

        // --- FUNCDIVERSITY (diversitytype) ---
        if (oviUser.getFuncDiversity() == null) {
            errors.rejectValue("funcDiversity", "obligatorio", "Hace falta indicar el tipo de diversidad funcional");
        }

        // --- DEPENDENCYGRADE (integer) CHECK (dependencygrade >= 1 AND dependencygrade <= 3) ---
        if (oviUser.getDependencyGrade() == null) {
            errors.rejectValue("dependencyGrade", "obligatorio", "Hace falta indicar el grado de dependencia");
        } else if (oviUser.getDependencyGrade() < 1 || oviUser.getDependencyGrade() > 3) {
            errors.rejectValue("dependencyGrade", "invalido", "El grado de dependencia debe ser 1, 2 o 3");
        }

        // --- USERNAME (character varying(50) - UNIQUE GLOBAL) ---
        if (oviUser.getUserName() == null || oviUser.getUserName().trim().isEmpty()) {
            errors.rejectValue("userName", "obligatorio", "Hace falta introducir un nombre de usuario");
        } else if (oviUser.getUserName().length() < 4 || oviUser.getUserName().length() > 50) {
            errors.rejectValue("userName", "longitud", "El nombre de usuario debe tener entre 4 y 50 caracteres");
        } else {
            String nombreUsuario = oviUser.getUserName();

            // 1. Que no exista en la propia tabla OVI (salvo que sea él mismo editando su perfil)
            OVIUser oviExistente = oviUserDao.getOVIUserByUsername(nombreUsuario);
            if (oviExistente != null && !oviExistente.getIdNumber().equals(oviUser.getIdNumber())) {
                errors.rejectValue("userName", "duplicado", "Este nombre de usuario ya está en uso. Por favor, elige otro.");
            }

            // 2. Que no exista en PAP_PATI
            if (papPatiDao.getPAP_PATIByUsername(nombreUsuario) != null) {
                errors.rejectValue("userName", "error.pappati", "Este nombre de usuario ya está registrado en el sistema. Elige otro.");
            }

            // 3. Que no exista en Instructor
            if (instructorDao.getInstructorByUserName(nombreUsuario) != null) {
                errors.rejectValue("userName", "error.instructor", "Este nombre de usuario ya está registrado en el sistema. Elige otro.");
            }
        }

        // --- USERPASSWORD (character varying(255)) ---
        if (oviUser.getUserPassword() == null || oviUser.getUserPassword().trim().isEmpty()) {
            errors.rejectValue("userPassword", "obligatorio", "Hace falta introducir una contraseña");
        } else if (oviUser.getUserPassword().length() < 6 || oviUser.getUserPassword().length() > 255) {
            errors.rejectValue("userPassword", "longitud", "La contraseña debe tener entre 6 y 255 caracteres");
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}