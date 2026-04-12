package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Instructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class InstructorRowMapper implements RowMapper<Instructor>{

    @Override
    public Instructor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setIdNumber(rs.getString("idnumber"));
        instructor.setName(rs.getString("name"));
        instructor.setSurname(rs.getString("surname"));
        instructor.setPhoneNumber(rs.getString("phone_number"));
        instructor.setEmail(rs.getString("email"));
        instructor.setAddress(rs.getString("address"));
        instructor.setFormation(rs.getString("formation"));
        instructor.setExperience(rs.getString("experience"));
        instructor.setDateBirth(rs.getObject("date_birth", LocalDate.class));
        instructor.setUserPassword(rs.getString("userpassword"));
        instructor.setUserName(rs.getString("username"));
        return instructor;
    }
}

