package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Activity;
import es.uji.ei1027.oviaplication.model.Estado;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityRowMapper  implements RowMapper<Activity> {
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setIdNumber(rs.getInt("idnumber"));
        activity.setName(rs.getString("name"));
        activity.setDate(rs.getObject("date", LocalDate.class));
        activity.setTime(rs.getObject("time", LocalTime.class));
        activity.setLocation(rs.getString("location"));
        activity.setCapacity(rs.getInt("capacity"));
        activity.setPrice(rs.getDouble("price"));
        activity.setDescription(rs.getString("description"));
        String estado = rs.getString("estado");
        if (estado != null) {
            activity.setEstado(Estado.valueOf(estado));
        } else {
            activity.setEstado(Estado.pendiente);
        }
        return activity;
    }
}