package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Chat;
import es.uji.ei1027.oviaplication.model.ChatDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatDetailsRowMapper implements RowMapper<ChatDetails> {
    @Override
    public ChatDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatDetails chatDetails = new ChatDetails();
        chatDetails.setIdMatch(rs.getInt("idmatch"));
        chatDetails.setNombreContacto(rs.getString("nombrecontacto"));

        // Obtenemos los nuevos campos
        if (rs.getTimestamp("last_message_date") != null) {
            chatDetails.setLastMessageDate(rs.getTimestamp("last_message_date").toLocalDateTime());
        }
        chatDetails.setUnreadCount(rs.getInt("unread_count"));

        return chatDetails;
    }
}
