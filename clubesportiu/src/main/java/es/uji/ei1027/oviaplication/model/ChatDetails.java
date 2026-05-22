package es.uji.ei1027.oviaplication.model;

public class ChatDetails {
    private int idMatch;
    private String nombreContacto; // El nombre del PAP o del OVI con el que hablas

    // Getters y Setters
    public int getIdMatch() {
        return idMatch;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }
}
