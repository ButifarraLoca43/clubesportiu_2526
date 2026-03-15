package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.PAP_PATI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

// TODO: Cambiar nombre DB

@Repository
public class PAP_PATIDao
{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addPAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("INSERT INTO PAP_PATI VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                pap_pati.getName(),
                pap_pati.getSurname(),
                pap_pati.getEmail(),
                pap_pati.getDateBirth(),
                pap_pati.getIdNumber(),
                pap_pati.getAddress(),
                pap_pati.getPhoneNumber(),
                pap_pati.getExperience(),
                pap_pati.getCurriculumVitae(),
                pap_pati.getUserPassword()
        );
    }


    public void deletePAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("DELETE FROM PAP_PATI WHERE idNumber =?",
                pap_pati.getIdNumber());
    }



    public void updatePAP_PATI(PAP_PATI pap_pati) {
        jdbcTemplate.update("UPDATE PAP_PATI SET  name=?, surname=?, email=?, dateBirth=?, address=?, phoneNumber=?, experience=?, curriculumVitae=?, userPassword=? WHERE idNumber=?",
                pap_pati.getName(),
                pap_pati.getSurname(),
                pap_pati.getEmail(),
                pap_pati.getDateBirth(),
                pap_pati.getAddress(),
                pap_pati.getPhoneNumber(),
                pap_pati.getExperience(),
                pap_pati.getCurriculumVitae(),
                pap_pati.getUserPassword(),
                pap_pati.getIdNumber()
        );
    }


    public PAP_PATI getPAP_PATI(String idNumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM PAP_PATI WHERE idNumber=?",
                    new PAP_PATIRowMapper(),
                    idNumber
            );
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<PAP_PATI> getPAP_PATIs() {
        try {
            return jdbcTemplate.query("SELECT * FROM PAP_PATI", new PAP_PATIRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
