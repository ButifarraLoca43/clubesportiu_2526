package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Nadador;
import es.uji.ei1027.clubesportiu.model.Prova;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProvaDao {

    //!!!! NO ESTA ACABADA !!!!

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la prova a la base de dades */
    public void addProva(Prova prova) {
        jdbcTemplate.update("INSERT INTO Prova VALUES(?, ?, ?, ?)",
                prova.getNom(),
                prova.getDescripcio(),
                prova.getTipus(),
                prova.getDate()
        );
    }

    /* Esborra la prova de la base de dades */
    public void deleteProva(Prova prova) {
        jdbcTemplate.update("DELETE FROM Prova WHERE nom =?",
                prova.getNom());
    }

    /* Actualitza els atributs del nadador
       (excepte el nom, que és la clau primària) */
    public void updateProva(Prova prova) {
        jdbcTemplate.update("UPDATE Nadador SET  num_federat=?, pais=?, edat=?, genere=? WHERE nom=?",
                prova.getNom(),
                prova.getDescripcio(),
                prova.getTipus(),
                prova.getDate()
        );
    }


    /* Obté el nadador amb el nom donat. Torna null si no existeix. */
    public Nadador getNadador(String nomNadador) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Nadador WHERE nom =?",
                    new NadadorRowMapper(),
                    nomNadador
            );
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les proves. Torna una llista buida si no n'hi ha cap. */
    public List<Prova> getProves() {
        try {
            return jdbcTemplate.query("SELECT * FROM Prova", new ProvaRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Prova>();
        }
    }

}
