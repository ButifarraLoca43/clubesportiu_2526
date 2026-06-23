package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ContractDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addContract(Contract contract) {
        jdbcTemplate.update(
                "INSERT INTO contract (idmatch, url) VALUES(?, ?)",
                contract.getIdmatch(),
                contract.getUrl()
        );
    }

    public void deleteContract(int idmatch) {
        jdbcTemplate.update(
                "DELETE FROM contract WHERE idmatch = ?",
                idmatch
        );
    }

    public void updateContract(Contract contract) {
        jdbcTemplate.update(
                "UPDATE contract SET url = ? WHERE idmatch = ?",
                contract.getUrl(),
                contract.getIdmatch()
        );
    }

    public Contract getContract(int idmatch) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM contract WHERE idmatch = ?",
                    new ContractRowMapper(),
                    idmatch
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Contract> getContracts() {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM contract",
                    new ContractRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}