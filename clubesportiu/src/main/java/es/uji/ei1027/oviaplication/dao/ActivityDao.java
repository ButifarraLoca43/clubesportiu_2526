package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ActivityDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addActivity(Activity activity) {
        jdbcTemplate.update(
                "INSERT INTO activity (date, time, location, capacity, price, description, name, estado, tipo) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, CAST(? AS estado_enum), CAST(? AS tipo_actividad))",
                activity.getDate(),
                activity.getTime(),
                activity.getLocation(),
                activity.getCapacity(),
                activity.getPrice(),
                activity.getDescription(),
                activity.getName(),
                activity.getEstado() != null ? activity.getEstado().name() : "pendiente",
                activity.getTipo().name()
        );
    }
    public void deleteActivity(int idNumber) {
        jdbcTemplate.update("DELETE FROM activity WHERE IDNumber = ?", idNumber);
    }

    public void updateAcivity(Activity activity) {
        jdbcTemplate.update(
                "UPDATE Activity SET date=?, time=?, location=?, capacity=?, price=?, " +
                        " description=?, name=?, estado=?::estado_enum, tipo=?::tipo_actividad WHERE IDNumber=?",
                activity.getDate(), activity.getTime(), activity.getLocation(),
                activity.getCapacity(), activity.getPrice(), (activity.getDescription() != null) ? activity.getDescription() : null,
                activity.getName(), activity.getEstado().name(), activity.getTipo().name(), activity.getIdNumber()
        );
    }

    public void updateEstado(int idNumber, Estado estado) {
        jdbcTemplate.update(
                "UPDATE activity SET estado = CAST(? AS estado_enum) WHERE idnumber = ?",
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

    public Object getInstructorActivitiesAcept(String idNumber) {
        try {
            return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN imparts i ON a.idnumber = i.idactivity WHERE i.idinstructor = ? AND i.estado = 'aceptado'",
                    new ActivityRowMapper(), idNumber);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getInstructorActivitiesPend(String idNumber) {
        try {
            return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN imparts i ON a.idnumber = i.idactivity WHERE i.idinstructor = ? AND i.estado = 'pendiente'",
                    new ActivityRowMapper(), idNumber);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getInstructorFutureActivitiesAcept(String idNumber) {
        try {
            return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN imparts i ON a.idnumber = i.idactivity WHERE i.idinstructor = ? AND i.estado = 'aceptado' AND a.date >= ?",
                    new ActivityRowMapper(), idNumber, LocalDate.now());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Map<Integer, Integer> getInstructorCounts() {
        try {
            String sql = "SELECT idactivity, COUNT(idinstructor) as total FROM imparts WHERE estado = 'aceptado' GROUP BY idactivity";
            Map<Integer, Integer> counts = new HashMap<>();
            jdbcTemplate.query(sql, rs -> {
                counts.put(rs.getInt("idactivity"), rs.getInt("total"));
            });

            return counts;
        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }

    public Object getMyActivities(String idNumber, TipoUsuario tipoUsuario) {
        try {
            if (tipoUsuario == TipoUsuario.OVIUser){
                return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idovi = ?",
                        new ActivityRowMapper(), idNumber);
            } else {
                if (tipoUsuario == TipoUsuario.PAP_PATI){
                    return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idpap = ?",
                        new ActivityRowMapper(), idNumber);
                } else {
                    if (tipoUsuario == TipoUsuario.instructor){
                        return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN imparts i ON a.idnumber = i.idactivity WHERE i.idinstructor = ?",
                                new ActivityRowMapper(), idNumber);
                    } else {
                        return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idext = ?",
                                new ActivityRowMapper(), idNumber);
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getMyFutureActivities(String idNumber, TipoUsuario tipoUsuario) {
        try {
            if (tipoUsuario == TipoUsuario.OVIUser){
                return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idovi = ? AND a.date >= ?",
                        new ActivityRowMapper(), idNumber, LocalDate.now());
            } else {
                if (tipoUsuario == TipoUsuario.PAP_PATI) {
                    return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idpap = ? AND a.date >= ?",
                            new ActivityRowMapper(), idNumber, LocalDate.now());
                } else {
                    if (tipoUsuario == TipoUsuario.instructor) {
                        return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN imparts i ON a.idnumber = i.idactivity WHERE i.idinstructor = ? AND a.date >= ?",
                                new ActivityRowMapper(), idNumber, LocalDate.now());
                    } else {
                        return jdbcTemplate.query("SELECT DISTINCT * FROM activity a JOIN inscription i ON a.idnumber = i.idactivity WHERE i.idext = ? AND a.date >= ?",
                                new ActivityRowMapper(), idNumber, LocalDate.now());
                    }
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getFutureActivitiesAcept() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity WHERE estado = 'aceptado' AND date >= ?",
                    new ActivityRowMapper(), LocalDate.now());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getActivitiesFormacion() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity WHERE tipo = 'formacion'",
                    new ActivityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public Object getActivitiesDivulgacion() {
        try {
            return jdbcTemplate.query("SELECT * FROM Activity WHERE tipo = 'divulgacion'",
                    new ActivityRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getInscritosByActivityId(int activityId) {
        String sql = "SELECT " +
                "  COALESCE(o.name || ' ' || o.surname, p.name || ' ' || p.surname, e.name || ' ' || e.surname) AS nombre, " +
                "  COALESCE(o.email, p.email, e.email) AS email, " +
                "  COALESCE(o.phonenumber, p.phonenumber, e.phonenumber) AS telefono, " +
                "  CASE " +
                "      WHEN i.idovi IS NOT NULL THEN 'OVIUser' " +
                "      WHEN i.idpap IS NOT NULL THEN 'PAP_PATI' " +
                "      WHEN i.idext IS NOT NULL THEN 'ExternalUser' " +
                "  END AS tipo_usuario " +
                "FROM inscription i " +
                "LEFT JOIN oviuser o ON i.idovi = o.idnumber " +
                "LEFT JOIN pap_pati p ON i.idpap = p.idnumber " +
                "LEFT JOIN external_activity_assistants e ON i.idext = e.idnumber " +
                "WHERE i.idactivity = ?";
        try {
            return jdbcTemplate.queryForList(sql, activityId);
        } catch (EmptyResultDataAccessException e) {
            // En caso de error en la consulta, devolvemos una lista vacía para que la web no explote
            return new ArrayList<>();
        }
    }
}