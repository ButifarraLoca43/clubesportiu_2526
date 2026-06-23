package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Contract;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ContractRowMapper implements RowMapper<Contract> {

    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();

        contract.setIdmatch(rs.getInt("idmatch"));
        contract.setUrl(rs.getString("url"));

        return contract;
    }
}