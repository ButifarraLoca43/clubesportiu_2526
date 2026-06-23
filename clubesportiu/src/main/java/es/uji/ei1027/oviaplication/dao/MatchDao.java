package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class MatchDao
{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addMatch(Match match) {
        jdbcTemplate.update("INSERT INTO Match (iduser, idpap, idrequest, date) VALUES (?, ?, ?, ?)",
                match.getIdUser(),
                match.getIdPAP(),
                match.getIdRequest(),
                match.getDate()
        );
    }

    public void deleteMatch(Match match)
    {
        jdbcTemplate.update("DELETE FROM Match WHERE idnumber =?",
                match.getIdNumber()
        );
    }

    public void updateMatch(Match match)
    {
        jdbcTemplate.update("UPDATE Match  SET iduser=?, idpap=?, idrequest=?, date=? WHERE idnumber=?",
                match.getIdUser(),
                match.getIdPAP(),
                match.getIdRequest(),
                match.getDate(),
                match.getIdNumber()
        );
    }

    public Match getMatch(String idnumber)
    {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Match WHERE idnumber=?",
                    new MatchRowMapper(),
                    idnumber
            );
        } catch(EmptyResultDataAccessException e) {
            return null;
        }

    }

    public List<Match> getMatches()
    {
        try {
            return jdbcTemplate.query("SELECT * FROM Match", new MatchRowMapper()
            );
        } catch (EmptyResultDataAccessException e)
        {
            return new ArrayList<>();
        }
    }

    public List<Match> getMatchesUser(String iduser)
    {
        try {
            return jdbcTemplate.query("SELECT * FROM Match WHERE iduser=?",
                    new MatchRowMapper(),
                    iduser
            );
        } catch (EmptyResultDataAccessException e)
        {
            return new ArrayList<>();
        }
    }

    public void updateEstado(int idRequest, String idpap, String nuevoEstado) {
        String sql = "UPDATE match SET emparejamiento = ?::emparejamiento_enum WHERE idrequest = ? AND idpap = ?";
        jdbcTemplate.update(sql, nuevoEstado, idRequest, idpap);
    }

    public void rejectOtherPAPs(int idRequest) {
        String sql = "UPDATE match SET emparejamiento = 'rechaza_OVI'::emparejamiento_enum " +
                "WHERE idrequest =? AND emparejamiento = 'pendiente_OVI'::emparejamiento_enum";

        jdbcTemplate.update(sql, idRequest);
    }

    public List<Map<String, Object>> getMatchesConNombres() {
        String sql = "SELECT m.idnumber, m.iduser, m.idpap, m.idrequest, m.date, m.emparejamiento AS estado, " +
                "u.name AS oviname, u.surname AS ovisurname, " +
                "p.name AS papname, p.surname AS papsurname " +
                "FROM match m " +
                "JOIN oviuser u ON m.iduser = u.idnumber " +
                "JOIN pap_pati p ON m.idpap = p.idnumber " +
                "ORDER BY m.date DESC";

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getMatchesConNombresAceptados() {
        String sql = "SELECT m.idnumber, m.iduser, m.idpap, m.idrequest, m.date, m.emparejamiento AS estado, " +
                "u.name AS oviname, u.surname AS ovisurname, " +
                "p.name AS papname, p.surname AS papsurname " +
                "FROM match m " +
                "JOIN oviuser u ON m.iduser = u.idnumber " +
                "JOIN pap_pati p ON m.idpap = p.idnumber " +
                "WHERE m.emparejamiento = 'aceptado_PAP' " +
                "ORDER BY m.date DESC";

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getMatchesConNombresByUser(String iduser) {
        String sql = "SELECT m.idnumber, m.iduser, m.idpap, m.idrequest, m.date, m.emparejamiento AS estado, " +
                "u.name AS oviname, u.surname AS ovisurname, " +
                "p.name AS papname, p.surname AS papsurname, " +
                "r.requiredsupport " +
                "FROM match m " +
                "JOIN oviuser u ON m.iduser = u.idnumber " +
                "JOIN pap_pati p ON m.idpap = p.idnumber " +
                "JOIN request_for_pap_pati r ON m.idrequest = r.idnumber " +
                "WHERE m.iduser = ? " +
                "ORDER BY m.date DESC";
        return jdbcTemplate.queryForList(sql, iduser);
    }

    public List<Map<String, Object>> getMatchesAceptadosByUser(String iduser) {
        String sql = "SELECT m.idnumber, m.iduser, m.idpap, m.idrequest, m.date, m.emparejamiento AS estado, " +
                "u.name AS oviname, u.surname AS ovisurname, " +
                "p.name AS papname, p.surname AS papsurname, " +
                "r.requiredsupport " +
                "FROM match m " +
                "JOIN oviuser u ON m.iduser = u.idnumber " +
                "JOIN pap_pati p ON m.idpap = p.idnumber " +
                "JOIN request_for_pap_pati r ON m.idrequest = r.idnumber " +
                "WHERE m.iduser = ? AND m.emparejamiento = 'aceptado_PAP' " +
                "ORDER BY m.date DESC";
        return jdbcTemplate.queryForList(sql, iduser);
    }
}
