package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.PAP_PATI;
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
public class PAP_PATIDao
{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addPAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("INSERT INTO pap_pati (name, surname, email, datebirth, idnumber, address, phonenumber, experience, curriculumvitae, userpassword, username, estado)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::estado_enum)",
                pap_pati.getName(),
                pap_pati.getSurname(),
                pap_pati.getEmail(),
                pap_pati.getDateBirth(),
                pap_pati.getIdNumber(),
                pap_pati.getAddress(),
                pap_pati.getPhoneNumber(),
                pap_pati.getExperience(),
                pap_pati.getCurriculumVitae(),
                pap_pati.getUserPassword(),
                pap_pati.getUserName(),
                pap_pati.getEstado().toString()
        );
    }


    public void deletePAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("DELETE FROM pap_pati WHERE idnumber =?",
                pap_pati.getIdNumber());
    }



    public void updatePAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("UPDATE pap_pati SET  name=?, surname=?, email=?, dateBirth=?, address=?, phoneNumber=?, experience=?, curriculumVitae=?, userPassword=?, username=?, estado=?::estado_enum WHERE idNumber=?",
                pap_pati.getName(),
                pap_pati.getSurname(),
                pap_pati.getEmail(),
                pap_pati.getDateBirth(),
                pap_pati.getAddress(),
                pap_pati.getPhoneNumber(),
                pap_pati.getExperience(),
                pap_pati.getCurriculumVitae(),
                pap_pati.getUserPassword(),
                pap_pati.getUserName(),
                pap_pati.getEstado().toString(),
                pap_pati.getIdNumber()
        );
    }


    public PAP_PATI getPAP_PATI(String idnumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM pap_pati WHERE idnumber=?",
                    new PAP_PATIRowMapper(),
                    idnumber
            );
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<PAP_PATI> getPAP_PATIs() {
        try {
            //return jdbcTemplate.query("SELECT * FROM pap_pati", new PAP_PATIRowMapper());
            return jdbcTemplate.query("SELECT * FROM pap_pati WHERE estado = 'aceptado'::estado_enum",
                    new PAP_PATIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public UserDetails loadUserByUsername(String username, String userpassword) {
        try {
            UserDetails user = jdbcTemplate.queryForObject(
                    "SELECT username, userpassword, estado FROM pap_pati WHERE username = ?",
                    new UserDetailsRowMapper(),
                    username
            );

            BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
            if (passwordEncryptor.checkPassword(userpassword, user.getUserPassword())) {
                user.setTipoUsuario(TipoUsuario.PAP_PATI);
                return user; // Login OK
            } else {
                return null; // Contraseña mal
            }

        } catch (EmptyResultDataAccessException e) {
            return null; // Usuario no existe
        }


    }
}
