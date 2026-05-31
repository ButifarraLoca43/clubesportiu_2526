package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDetailsRowMapper implements RowMapper<UserDetails> {
    @Override
    public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserName(rs.getString("username"));
        userDetails.setUserPassword(rs.getString("userpassword"));
        userDetails.setIdNumber(rs.getString("idnumber"));
        try {
            String estadoStr = rs.getString("estado");
            if (estadoStr != null) {
                userDetails.setEstado(Estado.fromValor(estadoStr));
            }
        } catch (SQLException e) {
            // Si la columna 'estado' no existe en la consulta, se quedará como null
        }

        return userDetails;
    }
}
