package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addActivity(Activity activity) {
        jdbcTemplate.update(
                "INSERT INTO activity (date, time, location, capacity, price, description, name, estado) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
                activity.getDate(),
                activity.getTime(),
                activity.getLocation(),
                activity.getCapacity(),
                activity.getPrice(),
                activity.getDescription(),
                activity.getName(),
                activity.getEstado() != null ? activity.getEstado().name() : "pendiente"
        );
    }

    public void deleteActivity(int idNumber) {
        jdbcTemplate.update("DELETE FROM activity WHERE IDNumber = ?", idNumber);
    }

    public void updateAcivity(Activity activity) {
        jdbcTemplate.update(
                "UPDATE Activity SET idnumber=?, date=?, time=?, location=?, capacity=?, price=?, " +
                        " description=?, name=?, estado=? WHERE IDNumber=?",
                activity.getIdNumber(), activity.getDate(), activity.getTime(), activity.getLocation(),
                activity.getCapacity(), activity.getPrice(), (activity.getDescription() != null) ? activity.getDescription() : null,
                activity.getName(), activity.getEstado(), activity.getIdNumber()
        );
    }

    public void updateEstado(int idNumber, Estado estado) {
        jdbcTemplate.update(
                "UPDATE Activity SET estado = ? WHERE idnumber = ?",
                estado.name(), idNumber
        );
    }


    public Activity getActivity(int idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Activity WHERE IDNumber = ?",
                    new ActivityRowMapper(),
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Object getActivitiesTodo() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity",
                    new ActivityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getActivitiesAcept() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity WHERE estado = 'aceptado'",
                    new ActivityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getActivitiesPend() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity WHERE estado = 'pendiente'",
                    new ActivityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}