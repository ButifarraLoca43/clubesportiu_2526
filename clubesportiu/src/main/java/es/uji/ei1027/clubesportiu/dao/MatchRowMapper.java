package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Match;
import es.uji.ei1027.clubesportiu.model.PAP_PATI;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class MatchRowMapper implements RowMapper<Match> {

    @Override
    public Match mapRow(ResultSet rs, int rowNum) throws SQLException {
        Match match = new Match();
        match.setId(rs.getInt("id"));
        match.setDate(rs.getObject("date", LocalDate.class));
        match.setIdUser(rs.getString("iduser"));
        match.setIdPAP(rs.getString("idpap"));
        match.setIdRequest(rs.getString("idrequest"));

        return match;
    }
}
