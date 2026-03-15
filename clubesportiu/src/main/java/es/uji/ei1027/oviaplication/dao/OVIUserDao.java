package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.OVIUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OVIUserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void addOVIUser(OVIUser user) {
        jdbcTemplate.update(
                "INSERT INTO OVIUser VALUES(?, ?, ?, ?, ?, ?, ?, ?::DiversityType, ?, ?, ?)",
                user.getName(),
                user.getSurname(),
                user.getDateBirth(),
                user.getIdNumber(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getAddress(),
                // Convertimos el Enum a String para la DB
                (user.getFuncDiversity() != null) ? user.getFuncDiversity().name() : null,
                user.getDependencyGrade(),
                user.getUserPassword(),
                user.getUserName()
        );
    }

    public void deleteOVIUser(String idNumber) {
        jdbcTemplate.update("DELETE FROM OVIUser WHERE IDNumber = ?", idNumber);
    }

    public void updateOVIUser(OVIUser user) {
        jdbcTemplate.update(
                "UPDATE OVIUser SET name=?, surname=?, dateBirth=?, phoneNumber=?, email=?, address=?, " +
                        "funcDiversity=?::DiversityType, dependencyGrade=?, userPassword=?, userName=? WHERE IDNumber=?",
                user.getName(), user.getSurname(), user.getDateBirth(), user.getPhoneNumber(),
                user.getEmail(), user.getAddress(),
                (user.getFuncDiversity() != null) ? user.getFuncDiversity().name() : null,
                user.getDependencyGrade(), user.getUserPassword(), user.getUserName(),
                user.getIdNumber()
        );
    }

    public OVIUser getOVIUser(String idNumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM OVIUser WHERE IDNumber = ?",
                    new OVIUserRowMapper(),
                    idNumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<OVIUser> getOVIUsers() {
        try {
            return jdbcTemplate.query("SELECT * FROM OVIUser", new OVIUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<OVIUser>();
        }
    }



}
