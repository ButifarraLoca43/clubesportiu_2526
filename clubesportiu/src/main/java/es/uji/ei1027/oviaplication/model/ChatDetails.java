package es.uji.ei1027.oviaplication.model;

import java.time.LocalDateTime;

public class ChatDetails {
    private int idMatch;
    private String nombreContacto;
    private LocalDateTime lastMessageDate;
    private int unreadCount;

    // Getters y Setters
    public int getIdMatch() { return idMatch; }
    public void setIdMatch(int idMatch) { this.idMatch = idMatch; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public LocalDateTime getLastMessageDate() { return lastMessageDate; }
    public void setLastMessageDate(LocalDateTime lastMessageDate) { this.lastMessageDate = lastMessageDate; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
