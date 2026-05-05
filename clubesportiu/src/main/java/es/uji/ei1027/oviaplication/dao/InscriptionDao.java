package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Inscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InscriptionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addInscription(Inscription inscription) {
        jdbcTemplate.update(
                "INSERT INTO Inscription (idovi, idpap, idext, idactivity) VALUES(?, ?, ?, ?)",
                inscription.getIdovi(),
                inscription.getIdpap(),
                inscription.getIdext(),
                inscription.getIdactivity()
        );
    }

    public void deleteInscription(int idNumber) {
        jdbcTemplate.update("DELETE FROM Inscription WHERE idnumber = ?", idNumber);
    }

    public void updateInscription(Inscription inscription) {
        jdbcTemplate.update(
                "UPDATE Inscription SET idovi=?, idpap=?, idext=?, idactivity=? WHERE idnumber=?",
                inscription.getIdovi(),
                inscription.getIdpap(),
                inscription.getIdext(),
                inscription.getIdactivity(),
                inscription.getIdNumber()
        );
    }

    public Inscription getInscription(int idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Inscription WHERE idnumber = ?",
                    new InscriptionRowMapper(),
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Inscription> getInscriptions() {
        try {
            return jdbcTemplate.query("SELECT * FROM Inscription", new InscriptionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}