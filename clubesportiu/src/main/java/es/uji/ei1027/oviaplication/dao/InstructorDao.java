package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Instructor;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InstructorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addInstructor(Instructor instructor) {
        jdbcTemplate.update(
                "INSERT INTO instructor (idnumber, name, surname, phone_number, email, address, formation, experience, date_birth, userpassword, username) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                instructor.getIdNumber(),
                instructor.getName(),
                instructor.getSurname(),
                instructor.getPhoneNumber(),
                instructor.getEmail(),
                instructor.getAddress(),
                instructor.getFormation(),
                instructor.getExperience(),
                instructor.getDateBirth(),
                instructor.getUserPassword(),
                instructor.getUserName()
        );
    }

    public void deleteInstructor(String idNumber) {
        jdbcTemplate.update("DELETE FROM instructor WHERE idnumber = ?", idNumber);
    }

    public void updateInstructor(Instructor instructor) {
        jdbcTemplate.update(
                "UPDATE instructor SET name=?, surname=?, phone_number=?, email=?, address=?, formation=?, experience=?, date_birth=?, userpassword=?, username=? WHERE idnumber=?",
                instructor.getName(),
                instructor.getSurname(),
                instructor.getPhoneNumber(),
                instructor.getEmail(),
                instructor.getAddress(),
                instructor.getFormation(),
                instructor.getExperience(),
                instructor.getDateBirth(),
                instructor.getUserPassword(),
                instructor.getUserName(),
                instructor.getIdNumber()
        );
    }

    public Instructor getInstructor(String idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM instructor WHERE idnumber = ?",
                    new InstructorRowMapper(),
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Instructor> getInstructors() {
        try {
            return jdbcTemplate.query("SELECT * FROM instructor", new InstructorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Instructor getInstructorByUserName(String userName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM instructor WHERE username = ?",
                    new InstructorRowMapper(),
                    userName
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Instructor getInstructorByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM instructor WHERE email = ?",
                    new InstructorRowMapper(),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserDetails loadUserByUsername(String username, String userpassword) {
        try {
            UserDetails user = jdbcTemplate.queryForObject(
                    "SELECT username, userpassword, idnumber FROM instructor WHERE username = ?",
                    new UserDetailsRowMapper(),
                    username
            );

            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
            if (passwordEncryptor.checkPassword(userpassword, user.getUserPassword())) {
                user.setTipoUsuario(TipoUsuario.instructor);
                return user; // Login OK
            } else {
                return null; // Contraseña mal
            }

        } catch (EmptyResultDataAccessException e) {
            return null; // Usuario no existe
        }
    }
}