package es.uji.ei1027.oviaplication.model;

import java.time.LocalDateTime;

public class Chat {
    private int idNumber;
    private String messageContent;
    private LocalDateTime timestamp;
    private String senderType; // Guardará "OVI" o "PAP"
    private int idMatch;

    public int getIdNumber() {
        return idNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSenderType() {
        return senderType;
    }

    public int getIdMatch() {
        return idMatch;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "idNumber=" + idNumber +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                ", senderType='" + senderType + '\'' +
                ", idMatch=" + idMatch +
                '}';
    }
}
