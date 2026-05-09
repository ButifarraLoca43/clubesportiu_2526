package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


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
}
