package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.ExternalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExternalUserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addExternalUser(ExternalUser user) {
        jdbcTemplate.update(
                "INSERT INTO external_user (idnumber, name, surname, email, phonenumber) VALUES(?, ?, ?, ?, ?)",
                user.getIdnumber(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhonenumber()
        );
    }

    public void updateExternalUser(ExternalUser user) {
        jdbcTemplate.update(
                "UPDATE external_user SET name=?, surname=?, email=?, phonenumber=? WHERE idnumber=?",
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhonenumber(),
                user.getIdnumber()
        );
    }

    public void deleteExternalUser(String idnumber) {
        jdbcTemplate.update("DELETE FROM external_user WHERE idnumber = ?", idnumber);
    }

    public ExternalUser getExternalUser(String idnumber) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM external_user WHERE idnumber = ?",
                    new ExternalUserRowMapper(),
                    idnumber
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<ExternalUser> getExternalUsers() {
        try {
            return jdbcTemplate.query("SELECT * FROM external_user", new ExternalUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}