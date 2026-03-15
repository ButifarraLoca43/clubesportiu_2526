package es.uji.ei1027.oviaplication;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.model.DiversityType;

import es.uji.ei1027.oviaplication.model.OVIUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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
public class OVIApplication implements CommandLineRunner { // 1. IMPORTANTE: Implementar CommandLineRunner

    private static final Logger log = Logger.getLogger(OVIApplication.class.getName());

    // 2. Inyectar el DAO correctamente
    @Autowired
    private OVIUserDao oviUserDao;

    public static void main(String[] args) {
        new SpringApplicationBuilder(OVIApplication.class).run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        log.info("--- Iniciando pruebas del OVIUserDao ---");
//
//        // 1. Crear un objeto de prueba
//        OVIUser nuevoUsuario = new OVIUser();
//        nuevoUsuario.setName("Marc");
//        nuevoUsuario.setSurname("Vilanova");
//        nuevoUsuario.setDateBirth(LocalDate.of(1995, 3, 12));
//        nuevoUsuario.setIdNumber("12345678A");
//        nuevoUsuario.setEmail("marc@uji.es");
//        nuevoUsuario.setFuncDiversity(DiversityType.physical);
//        nuevoUsuario.setDependencyGrade(2);
//        nuevoUsuario.setUserPassword("password123");
//        nuevoUsuario.setUserName("mvilanova");
//
//        // 2. Probar la inserción (Usando la instancia 'oviUserDao', no la clase)
//        log.info("Insertando usuario...");
//        oviUserDao.addOVIUser(nuevoUsuario);
//
//        // 3. Probar la lectura de todos
//        log.info("Listado de usuarios en la base de datos:");
//        List<OVIUser> lista = oviUserDao.getOVIUsers();
//        for (OVIUser u : lista) {
//            log.info("Usuario encontrado: " + u.getName() + " " + u.getSurname() + " (" + u.getIdNumber() + ")");
//        }
//
//        // 4. Probar la búsqueda individual
//        log.info("Buscando al usuario con ID 12345678A...");
//        OVIUser buscado = oviUserDao.getOVIUser("12345678A");
//        if (buscado != null) {
//            log.info("¡Encontrado! Email: " + buscado.getEmail());
//        } else {
//            log.warning("Usuario no encontrado.");
//        }
//
//        log.info("--- Pruebas finalizadas ---");
    }
}

