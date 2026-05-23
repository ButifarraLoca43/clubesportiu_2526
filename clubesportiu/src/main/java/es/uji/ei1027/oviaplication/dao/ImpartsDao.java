package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Imparts;
import es.uji.ei1027.oviaplication.model.Inscription;
import es.uji.ei1027.oviaplication.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getInstructorRequestsByActivity(int idActivity) {
        // SQL configurado con la tabla 'instructor' y columnas 'name'/'surname'
        String sql = "SELECT i.idinstructor, i.estado, inst.name, inst.surname " +
                "FROM imparts i " +
                "LEFT JOIN instructor inst ON i.idinstructor = inst.idnumber " +
                "WHERE i.idactivity = ?";
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("idInstructor", rs.getString("idinstructor"));
                map.put("estado", rs.getString("estado"));

                // Guardamos el nombre y apellidos recuperados en el Map
                map.put("nombreInstructor", rs.getString("name"));
                map.put("apellidosInstructor", rs.getString("surname"));

                return map;
            }, idActivity);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // Método CORREGIDO: Añadido el CAST a estado_enum por si usas PostgreSQL
    public void updateImpartsEstado(int idActivity, String idInstructor, String nuevoEstado) {
        String sql = "UPDATE imparts SET estado = CAST(? AS estado_enum) WHERE idactivity = ? AND idinstructor = ?";
        jdbcTemplate.update(sql, nuevoEstado, idActivity, idInstructor);
    }

    public Instructor getInstructor(String idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM instructor WHERE idnumber = ?",
                    new InstructorRowMapper(), // Asumiendo que tienes un RowMapper para Instructor
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}