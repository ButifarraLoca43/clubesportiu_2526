package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Classificacio;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public final class ClassificacioRowMapper implements
        RowMapper<Classificacio> {

    public Classificacio mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        Classificacio classificacio = new Classificacio();
        classificacio.setNomNadador(rs.getString("nom_nadador"));
        classificacio.setNomProva(rs.getString("nom_prova"));
        classificacio.setPosicio(rs.getInt("posicio"));
        classificacio.setTemps(rs.getObject("temps", LocalTime.class));
        return classificacio;
    }
}

