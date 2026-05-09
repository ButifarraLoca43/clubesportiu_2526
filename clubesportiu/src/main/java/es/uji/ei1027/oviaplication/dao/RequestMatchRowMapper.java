package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.EstadoMatch;
import es.uji.ei1027.oviaplication.model.RequestMatch;
import org.springframework.jdbc.core.RowMapper;

public class RequestMatchRowMapper implements RowMapper<RequestMatch> {
    @Override
    public RequestMatch mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        RequestMatch requestMatch = new RequestMatch();
        requestMatch.setIdnumber(rs.getInt("idnumber"));
        requestMatch.setIduser(rs.getString("iduser"));
        requestMatch.setDate(rs.getObject("date", java.time.LocalDate.class));
        requestMatch.setRequiredsupport(rs.getString("requiredsupport"));
        requestMatch.setDescription(rs.getString("description"));
        requestMatch.setRequirements(rs.getString("requirements"));
        requestMatch.setLifeproject(rs.getString("lifeproject"));
        requestMatch.setIdpap(rs.getString("idpap"));

        String estadoStr = rs.getString("emparejamiento");
        if (estadoStr != null) {
            requestMatch.setEmparejamiento(EstadoMatch.fromValor(estadoStr));
        }

        return requestMatch;
    }
}
