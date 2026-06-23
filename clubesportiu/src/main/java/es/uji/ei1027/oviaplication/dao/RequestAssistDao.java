package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.RequestAssist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class RequestAssistDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addRequestAssist(RequestAssist requestAssist) {
        jdbcTemplate.update("INSERT INTO request_for_pap_pati (iduser, date, requiredsupport, description, requirements, lifeproject) VALUES (?, ?, ?, ?, ?, ?)",
                requestAssist.getIduser(),
                requestAssist.getDate(),
                requestAssist.getRequiredsupport(),
                requestAssist.getDescription(),
                requestAssist.getRequirements(),
                requestAssist.getLifeproject()
        );
    }

    public void deleteRequestAssist(RequestAssist requestAssist)
    {
        jdbcTemplate.update("DELETE FROM request_for_pap_pati WHERE idnumber =?",
                requestAssist.getIdnumber()
        );
    }

    public void updateRequestAssist(RequestAssist requestAssist)
    {
        jdbcTemplate.update("UPDATE request_for_pap_pati  SET iduser=?, date=?, requiredsupport=?, description=?, requirements=?, lifeproject=? WHERE idnumber=?",
                requestAssist.getIduser(),
                requestAssist.getDate(),
                requestAssist.getRequiredsupport(),
                requestAssist.getDescription(),
                requestAssist.getRequirements(),
                requestAssist.getLifeproject(),
                requestAssist.getIdnumber()
        );
    }

    public RequestAssist getRequestAssist(int idnumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM request_for_pap_pati WHERE idnumber=?",
                    new RequestAssistRowMapper(),
                    idnumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RequestAssist> getRequestAssists() {
        try {
            return jdbcTemplate.query("SELECT * FROM request_for_pap_pati", new RequestAssistRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<RequestAssist> getRequestAssistsPorEstado(String estado) {
        try {
            return jdbcTemplate.query("SELECT * FROM request_for_pap_pati WHERE estado = ?::estado_enum",
                    new RequestAssistRowMapper(),
                    estado
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void updateEstado(int idnumber, String nuevoEstado) {
        jdbcTemplate.update("UPDATE request_for_pap_pati SET estado = ?::estado_enum WHERE idnumber = ?", nuevoEstado, idnumber);
    }

    public List<Map<String, Object>> getRequestAssistsPorEstadoConNombre(String estado) {
        String sql = "SELECT r.idnumber, r.iduser, r.date, r.requiredsupport, r.description, " +
                "r.requirements, r.lifeproject, r.estado, u.name AS oviname, u.surname AS ovisurname " +
                "FROM request_for_pap_pati r " +
                "JOIN oviuser u ON r.iduser = u.idnumber " +
                "WHERE r.estado = ?::estado_enum " +
                "ORDER BY r.date DESC";

        return jdbcTemplate.queryForList(sql, estado);
    }
}