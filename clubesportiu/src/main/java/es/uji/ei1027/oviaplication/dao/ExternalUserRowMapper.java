package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.ExternalUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExternalUserRowMapper implements RowMapper<ExternalUser> {
    @Override
    public ExternalUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExternalUser user = new ExternalUser();

        user.setIdnumber(rs.getString("idnumber"));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setEmail(rs.getString("email"));
        user.setPhonenumber(rs.getString("phonenumber"));

        return user;
    }
}