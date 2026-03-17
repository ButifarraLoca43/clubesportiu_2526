package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Match;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class MatchRowMapper implements RowMapper<Match> {

    @Override
    public Match mapRow(ResultSet rs, int rowNum) throws SQLException {
        Match match = new Match();
        match.setIdNumber(rs.getString("idnumber"));
        match.setIdUser(rs.getString("iduser"));
        match.setIdPAP(rs.getString("idpap"));
        match.setIdRequest(rs.getString("idrequest"));
        match.setDate(rs.getObject("date", LocalDate.class));

        return match;
    }
}
