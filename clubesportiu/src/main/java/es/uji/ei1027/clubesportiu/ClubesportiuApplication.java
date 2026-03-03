package es.uji.ei1027.clubesportiu;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

//@SpringBootApplication
//public class ClubesportiuApplication implements CommandLineRunner {
//
//    // Plantilla per a executar operacions sobre la connexió
//    private JdbcTemplate jdbcTemplate;
//    private NadadorDao nadadorDao;
//
//    // Crea el jdbcTemplate a partir del DataSource que hem configurat
//    @Autowired
//    public void setDataSource(DataSource dataSource) {
//
//        jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//    private static final Logger log = Logger.getLogger(ClubesportiuApplication.class.getName());
//
//    public static void main(String[] args) {
//        // Auto-configura l'aplicació
//        new SpringApplicationBuilder(ClubesportiuApplication.class).run(args);
//    }
//
//    // Funció principal
//    public void run(String... strings) throws Exception {
//
//
//        log.info("Iniciant la prova del DAO...");
//
//        // Llamamos a la función de prueba
//        provaNadadorDao();
//
//        log.info("Prova finalitzada.");
//
//
//        // Exemple de consulta de inserció.
////		log.info("Inserta una nova nadadora");
////		jdbcTemplate.update(
////				"INSERT INTO Nadador VALUES(?, ?, ?, ?, ?)",
////				"Ariadna Edo", "XX1242", "Espanya", null, "Femení");
////		log.info("I comprova que s'haja inserit correctament");
////		mostraNadador("Ariadna Edo");
////
////		log.info("Actualitza l'edat de la nadadora Ariadna Edo a 21 anys");
////		jdbcTemplate.update("UPDATE Nadador SET edat = 21 WHERE nom = 'Ariadna Edo'");
////
////
////		log.info("I comprova que s'haja modificat correctament");
////		mostraNadador("Ariadna Edo");
////
////		log.info("Esborra la nadadora Ariadna Edo");
////		jdbcTemplate.update("DELETE FROM Nadador WHERE nom = 'Ariadna Edo' ");
////		log.info("I comprova que s'haja esborrat correctament");
////		mostraNadador("Ariadna Edo");
//
////		mostraNadador("Gemma Mengual");
////		mostraNadador("Mireia Belmonte Garcia");
////		mostraNadador("No estic");
//    }
//
//    private void mostraNadador(String nomNadador) {
//        try {
//            Nadador n = jdbcTemplate.queryForObject(
//                    "SELECT * FROM Nadador WHERE nom =?",
//                    new NadadorRowMapper(),
//                    nomNadador
//            );
//            log.info(n.toString());
//        } catch (EmptyResultDataAccessException e) {
//            log.info("No es troba la DB");
//        }
//    }
//
//    public void provaNadadorDao() {
//        log.info("Provant NadadorDao");
//
//        log.info("Tots els nadadors:");
//        for (Nadador n : nadadorDao.getNadadors()) {
//            log.info(n.toString());
//        }
//        log.info("\n");
//
//        log.info("Dades de Gemma Mengual");
//        Nadador n = nadadorDao.getNadador("Gemma Mengual");
//        log.info(n.toString());
//        log.info("\n");
//
//        Nadador aEdo = new Nadador();
//        aEdo.setNom("Ariadna Edo");
//        aEdo.setEdat(21);
//        log.info("Nou: Ariadna Edo");
//        nadadorDao.addNadador(aEdo);
//        log.info(nadadorDao.getNadador("Ariadna Edo").toString());
//        log.info("\n");
//
//        log.info("Actualitzat: Ariadna Edo");
//        aEdo.setPais("Espanya");
//        aEdo.setGenere("Femení");
//        nadadorDao.updateNadador(aEdo);
//        log.info(nadadorDao.getNadador("Ariadna Edo").toString());
//        log.info("\n");
//
//        log.info("Esborrat: Ariadna Edo");
//        nadadorDao.deleteNadador(aEdo);
//        if (nadadorDao.getNadador("Ariadna Edo") == null) {
//            log.info("Esborrada correctament");
//        }
//        log.info("\n");
//    }
//
//}


@SpringBootApplication
public class ClubesportiuApplication {

    private static final Logger log =
            Logger.getLogger(ClubesportiuApplication.class.getName());

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(ClubesportiuApplication.class).run(args);
    }
}

