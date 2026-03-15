package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

// TODO: Cambiar nombre DB

@Repository
public class MatchDao
{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addMatch(Match match) {
        jdbcTemplate.update("INSERT INTO Match VALUES(?, ?, ?, ?)",
                match.getDate(),
                match.getIdUser(),
                match.getIdPAP(),
                match.getIdRequest()
        );
    }

    public void deleteMatch(Match match)
    {
        jdbcTemplate.update("DELETE FROM Match WHERE id =?",
                match.getId()
        );
    }

    public void updateMatch(Match match)
    {
        jdbcTemplate.update("UPDATE Match SET date=?, idUser=?, idPAP=?, idRequest=? WHERE id=?",
                match.getDate(),
                match.getIdUser(),
                match.getIdPAP(),
                match.getIdRequest(),
                match.getId()
        );
    }

    public Match getMatch(int id)
    {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Match WHERE id=?",
                    new MatchRowMapper(),
                    id
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
}
