package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Inscription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InscriptionRowMapper implements RowMapper<Inscription> {
    @Override
    public Inscription mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inscription inscription = new Inscription();

        inscription.setIdNumber(rs.getInt("idnumber"));
        inscription.setIdovi(rs.getString("idovi"));
        inscription.setIdpap(rs.getString("idpap"));
        inscription.setIdext(rs.getString("idext"));
        inscription.setIdactivity(rs.getInt("idactivity"));

        return inscription;
    }
}