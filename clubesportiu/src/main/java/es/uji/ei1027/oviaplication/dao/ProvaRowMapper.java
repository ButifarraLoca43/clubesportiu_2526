package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Prova;
import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class ProvaRowMapper  implements RowMapper<Prova> {

    @Override
    public Prova mapRow(ResultSet rs, int rowNum) throws SQLException {
        Prova prova = new Prova();
        prova.setNom("nom");
        prova.setDescripcio("descripcio");
        prova.setTipus("tipus");
        prova.setDate(rs.getObject("data", LocalDate.class));
        return prova;
    }
}
