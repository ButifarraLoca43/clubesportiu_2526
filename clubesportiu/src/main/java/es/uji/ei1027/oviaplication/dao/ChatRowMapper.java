package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Chat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ChatRowMapper implements RowMapper<Chat> {
    @Override
    public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
        Chat chat = new Chat();
        chat.setIdNumber(rs.getInt("idnumber"));
        chat.setMessageContent(rs.getString("messagecontent"));
        chat.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        chat.setSenderType(rs.getString("sendertype"));
        chat.setIdMatch(rs.getInt("idmatch"));
        return chat;
    }
}

