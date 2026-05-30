package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Chat;
import es.uji.ei1027.oviaplication.model.ChatDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 1. Añadir un mensaje a la base de datos
    public void addMessage(Chat chat) {
        String sql = "INSERT INTO chat (messagecontent, timestamp, sendertype, idmatch) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                chat.getMessageContent(),
                chat.getTimestamp(),
                chat.getSenderType(),
                chat.getIdMatch());
    }

    // 2. Obtener los mensajes de un chat (Usando ChatRowMapper)
    public List<Chat> getMessagesByMatch(int idMatch) {
        try {
            String sql = "SELECT * FROM chat WHERE idmatch = ? ORDER BY timestamp ASC";
            return jdbcTemplate.query(sql, new ChatRowMapper(), idMatch);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 3. Obtener lista de chats para un OVI User (Usando ChatSummaryRowMapper)
    public List<ChatDetails> getChatsForOviUser(String usernameOvi) {
        try {
            String sql = "SELECT MAX(m.idnumber) AS idmatch, p.username AS nombrecontacto " +
                    "FROM match m " +
                    "JOIN oviuser o ON m.iduser = o.idnumber " +
                    "JOIN pap_pati p ON p.idnumber = m.idpap " +
                    "WHERE o.username = ? " +
                    "AND m.emparejamiento != 'rechaza_OVI' " +
                    "GROUP BY p.username";
            return jdbcTemplate.query(sql, new ChatDetailsRowMapper(), usernameOvi);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 4. Obtener lista de chats para un PAP User (Usando ChatSummaryRowMapper)
    public List<ChatDetails> getChatsForPapPati(String usernamePap) {
        try {
            String sql = "SELECT MAX(m.idnumber) AS idmatch, o.username AS nombrecontacto " +
                    "FROM match m " +
                    "JOIN oviuser o ON m.iduser = o.idnumber " +
                    "JOIN pap_pati p ON p.idnumber = m.idpap " +
                    "WHERE p.username = ? " +
                    "AND m.emparejamiento != 'rechaza_OVI' " +
                    "GROUP BY o.username";
            return jdbcTemplate.query(sql, new ChatDetailsRowMapper(), usernamePap);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public boolean existsMatchParticipation(int idMatch, String id, String role) {
        try {
            String sql = "";
            if (role.equals("OVI")) {
                // Revisa si las columnas de tu tabla Match se llaman así
                sql = "SELECT COUNT(*) FROM match WHERE idnumber = ? AND iduser = ?";
            } else {
                sql = "SELECT COUNT(*) FROM match WHERE idnumber = ? AND idpap = ?";
            }

            int count = jdbcTemplate.queryForObject(sql, Integer.class, idMatch, id);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
