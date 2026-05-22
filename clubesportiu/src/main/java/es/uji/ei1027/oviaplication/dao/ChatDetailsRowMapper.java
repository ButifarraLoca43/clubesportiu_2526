package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.Chat;
import es.uji.ei1027.oviaplication.model.ChatDetails;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatDetailsRowMapper implements RowMapper<ChatDetails> {
    @Override
    public ChatDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatDetails summary = new ChatDetails();
        summary.setIdMatch(rs.getInt("idmatch"));
        summary.setNombreContacto(rs.getString("nombrecontacto"));
        return summary;
    }
}
