package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Imparts;
import es.uji.ei1027.oviaplication.model.Inscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ImpartsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int addImparts(Imparts imparts) {
        String sql = "INSERT INTO imparts (idactivity, idinstructor, estado) " +
                "VALUES (?, ?, CAST(? AS estado_enum)) " +
                "ON CONFLICT (idactivity, idinstructor) DO NOTHING";
        return jdbcTemplate.update(
                sql,
                imparts.getIdActivity(),
                imparts.getIdInstructor(),
                (imparts.getEstado() != null) ? imparts.getEstado().name() : "pendiente"
        );
    }



    public void deleteImparts(int idNumber) {
        jdbcTemplate.update("DELETE FROM Imparts WHERE idNumber = ?", idNumber);
    }

    public void updateImparts(Imparts imparts) {
        jdbcTemplate.update(
                "UPDATE imparts SET idactivity = ?, idinstructor = ?, estado = ? WHERE idnumber = ?",
                imparts.getIdActivity(),
                imparts.getIdInstructor(),
                (imparts.getEstado() != null) ? imparts.getEstado().name() : "pendiente",
                imparts.getIdNumber()
        );
    }


    public Imparts getImparts(int idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Imparts WHERE idnumber = ?",
                    new ImpartsRowMapper(),
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Imparts> getImparts() {
        try {
            return jdbcTemplate.query("SELECT * FROM Imparts", new ImpartsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Imparts> getImpartsFromInstructor(String idInstructor) {
        try {
            return jdbcTemplate.query("SELECT * FROM Imparts WHERE idInstructor = ?", new ImpartsRowMapper(), idInstructor);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Imparts> getImpartsFromActivity(int idActivity) {
        try {
            return jdbcTemplate.query("SELECT * FROM Imparts WHERE idActivity = ?", new ImpartsRowMapper(), idActivity);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}