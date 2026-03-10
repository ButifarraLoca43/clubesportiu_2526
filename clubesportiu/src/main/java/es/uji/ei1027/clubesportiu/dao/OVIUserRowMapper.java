package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.DiversityType;
import es.uji.ei1027.clubesportiu.model.OVIUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class OVIUserRowMapper implements RowMapper<OVIUser> {
    public OVIUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OVIUser user = new OVIUser();
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        user.setDateBirth(rs.getObject("datebirth", LocalDate.class));
        user.setIdNumber(rs.getString("idnumber"));
        user.setPhoneNumber(rs.getString("phonenumber"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        String diversityStr = rs.getString("funcdiversity");
        if (diversityStr != null) {
            user.setFuncDiversity(DiversityType.valueOf(diversityStr));
        }
        user.setDependencyGrade(rs.getInt("dependencygrade"));
        user.setUserPassword(rs.getString("userpassword"));
        user.setUserName(rs.getString("username"));
        return user;
    }
}
