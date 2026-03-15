package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Classificacio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClassificacioDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la classificacio a la base de dades */
    public void addClassificacio(Classificacio classificacio) {
        jdbcTemplate.update("INSERT INTO Classificacio VALUES(?, ?, ?, ?)",
                classificacio.getNomNadador(), classificacio.getNomProva(), classificacio.getPosicio(), classificacio.getTemps());
    }

    /* Esborra la classificacio de la base de dades */
    public void deleteClassificacio(String nomNadador, String nomProva) {
        jdbcTemplate.update("DELETE from Classificacio where nom_nadador=? AND nom_prova=?",
                nomNadador, nomProva);
    }
    public void deleteClassificacio(Classificacio classificacio) {
        jdbcTemplate.update("DELETE from Classificacio where nom_nadador=? AND nom_prova=?",
                classificacio.getNomNadador(), classificacio.getNomProva());
    }

    /* Actualitza els atributs de la classificacio
       (excepte el nomNadador y nomProva, que és la clau primària) */
    public void updateClassificacio(Classificacio classificacio) {
        jdbcTemplate.update("UPDATE Classificacio SET posicio=?, temps=? where nom_nadador=? and nom_prova=?",
                classificacio.getPosicio(), classificacio.getTemps(), classificacio.getNomNadador(), classificacio.getNomProva());
    }

    /* Obté la classificacio amb el nom donat. Torna null si no existeix. */
    public Classificacio getClassificacio(String nomNadador, String nomProva) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Classificacio WHERE nom_nadador=? and nom_prova=?",
                    new ClassificacioRowMapper(), nomNadador, nomProva);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les classificacions. Torna una llista buida si no n'hi ha cap. */
    public List<Classificacio> getClassificacions() {
        try {
            return jdbcTemplate.query("SELECT * from Classificacio",
                    new ClassificacioRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Classificacio>();
        }
    }

    public List<Classificacio> getClassificacioProva(String nomProva) {
        try {
            return this.jdbcTemplate.query(
                    "SELECT * FROM classificacio WHERE nom_prova=?",
                    new ClassificacioRowMapper(), nomProva);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Classificacio>();
        }
    }


}
