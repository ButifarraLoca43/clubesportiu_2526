package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class PAP_PATIDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addPAP_PATI(PAP_PATI pap_pati) {
        String sql = "INSERT INTO pap_pati (name, surname, email, datebirth, idnumber, address, " +
                "phonenumber, experience, curriculumvitae, userpassword, username, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::estado_enum)";

        jdbcTemplate.update(sql,
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
                pap_pati.getEstado() != null ? pap_pati.getEstado().name() : "pendiente"
        );
    }




    public void deletePAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("DELETE FROM pap_pati WHERE idnumber =?",
                pap_pati.getIdNumber());
    }



    public void updatePAP_PATI(PAP_PATI pap_pati) {
        String sql = "UPDATE pap_pati SET name=?, surname=?, email=?, datebirth=?, address=?, " +
                "phonenumber=?, experience=?, curriculumvitae=?, userpassword=?, username=?, estado=?::estado_enum " +
                "WHERE idnumber=?";

        jdbcTemplate.update(sql,
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
                pap_pati.getEstado() != null ? pap_pati.getEstado().name() : "pendiente",
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
            return jdbcTemplate.query("SELECT * FROM pap_pati WHERE estado = 'aceptado'",
                    new PAP_PATIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public UserDetails loadUserByUsername(String username, String userpassword) {
        try {
            UserDetails user = jdbcTemplate.queryForObject(
                    "SELECT username, userpassword, idNumber FROM pap_pati WHERE username = ?",
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

    public PAP_PATI getPAP_PATIByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM pap_pati WHERE userName = ?",
                    new PAP_PATIRowMapper(),
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Map<String, Object>> getPendingMatchesForPap(String idpap) {
        String sql = "SELECT u.name AS oviname, u.email AS oviemail, m.date, " +
                "r.requiredsupport, r.description, r.requirements, r.lifeproject, " +
                "m.idrequest, m.idpap, m.idnumber AS idmatch, m.emparejamiento " +
                "FROM match m " +
                "JOIN request_for_pap_pati r ON m.idrequest = r.idnumber " +
                "JOIN oviuser u ON r.iduser = u.idnumber " +
                "WHERE m.idpap = ? AND m.emparejamiento IN " +
                "('pendiente_PAP'::emparejamiento_enum, 'pendiente_OVI'::emparejamiento_enum) " +
                "ORDER BY m.date DESC";

        return jdbcTemplate.queryForList(sql, idpap);
    }

    public List<Map<String, Object>> getActiveMatchesForPap(String idpap) {
        String sql = "SELECT u.name AS oviname, u.email AS oviemail, m.date, " +
                "r.requiredsupport, r.description, r.requirements, r.lifeproject, " +
                "m.idrequest, m.idpap " +
                "FROM match m " +
                "JOIN request_for_pap_pati r ON m.idrequest = r.idnumber " +
                "JOIN oviuser u ON r.iduser = u.idnumber " +
                "WHERE m.idpap = ? AND m.emparejamiento = 'aceptado_PAP'::emparejamiento_enum " +
                "ORDER BY m.date DESC";

        return jdbcTemplate.queryForList(sql, idpap);
    }
}
