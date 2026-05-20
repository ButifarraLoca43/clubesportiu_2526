package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.ExternalUser;
import es.uji.ei1027.oviaplication.model.Imparts;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImpartsRowMapper implements RowMapper<Imparts> {
    @Override
    public Imparts mapRow(ResultSet rs, int rowNum) throws SQLException {
        Imparts imparts = new Imparts();

        imparts.setIdNumber(rs.getInt("idNumber"));
        imparts.setIdInstructor(rs.getString("idInstructor"));
        imparts.setIdActivity(rs.getInt("idActivity"));
        String estado = rs.getString("estado");
        if (estado != null) {
            imparts.setEstado(Estado.valueOf(estado));
        } else {
            imparts.setEstado(Estado.pendiente);
        }

        return imparts;
    }
}
