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

    public List<Chat> getMessagesByMatch(int idMatch) {
        try {
            String sql = "SELECT * FROM chat WHERE idmatch = ? ORDER BY timestamp ASC";
            return jdbcTemplate.query(sql, new ChatRowMapper(), idMatch);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void markMessagesAsRead(int idMatch, String currentSenderType) {
        String sql = "UPDATE chat SET is_read = true WHERE idmatch = ? AND sendertype != ?";
        jdbcTemplate.update(sql, idMatch, currentSenderType);
    }

    public List<ChatDetails> getChatsForOviUser(String usernameOvi) {
        try {
            String sql = "SELECT MAX(m.idnumber) AS idmatch, p.username AS nombrecontacto, " +
                    "MAX(c.timestamp) AS last_message_date, " +
                    "SUM(CASE WHEN c.is_read = false AND c.sendertype != 'OVI' THEN 1 ELSE 0 END) AS unread_count " +
                    "FROM match m " +
                    "JOIN oviuser o ON m.iduser = o.idnumber " +
                    "JOIN pap_pati p ON p.idnumber = m.idpap " +
                    "LEFT JOIN chat c ON m.idnumber = c.idmatch " +
                    "WHERE o.username = ? AND m.emparejamiento != 'rechaza_OVI' " +
                    "GROUP BY p.username " +
                    "ORDER BY last_message_date DESC"; // ORDENADO POR RECIENTES
            return jdbcTemplate.query(sql, new ChatDetailsRowMapper(), usernameOvi);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    // 4. Obtener lista de chats para un PAP User
    public List<ChatDetails> getChatsForPapPati(String usernamePap) {
        try {
            String sql = "SELECT MAX(m.idnumber) AS idmatch, o.username AS nombrecontacto, " +
                    "MAX(c.timestamp) AS last_message_date, " +
                    "SUM(CASE WHEN c.is_read = false AND c.sendertype != 'PAP' THEN 1 ELSE 0 END) AS unread_count " +
                    "FROM match m " +
                    "JOIN oviuser o ON m.iduser = o.idnumber " +
                    "JOIN pap_pati p ON p.idnumber = m.idpap " +
                    "LEFT JOIN chat c ON m.idnumber = c.idmatch " +
                    "WHERE p.username = ? AND m.emparejamiento != 'rechaza_OVI' " +
                    "GROUP BY o.username " +
                    "ORDER BY last_message_date DESC"; // ORDENADO POR RECIENTES
            return jdbcTemplate.query(sql, new ChatDetailsRowMapper(), usernamePap);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public boolean existsMatchParticipation(int idMatch, String id, String role) {
        try {
            String sql = "";
            if (role.equals("OVI")) {
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
