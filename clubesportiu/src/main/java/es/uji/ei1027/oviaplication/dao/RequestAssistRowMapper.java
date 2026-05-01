package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Match;
import es.uji.ei1027.oviaplication.model.RequestAssist;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RequestAssistRowMapper  implements RowMapper<RequestAssist> {
    @Override
    public RequestAssist mapRow(ResultSet rs, int rowNum) throws SQLException {
        RequestAssist requestAssist = new RequestAssist();
        requestAssist.setIdnumber(rs.getInt("idnumber"));
        requestAssist.setIduser(rs.getString("iduser"));
        requestAssist.setDate(rs.getObject("date", LocalDate.class));
        requestAssist.setRequiredsupport(rs.getString("requiredsupport"));
        requestAssist.setDescription(rs.getString("description"));
        requestAssist.setRequirements(rs.getString("requirements"));
        requestAssist.setLifeproject(rs.getString("lifeproject"));

        return requestAssist;
    }
}
