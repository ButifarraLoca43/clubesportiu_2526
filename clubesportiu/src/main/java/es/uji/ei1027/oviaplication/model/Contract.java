package es.uji.ei1027.oviaplication.model;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class Contract {

    private int idmatch;
    private String url;

    // Constructor vacío (necesario para Spring/JDBCTemplate)
    public Contract() {
    }

    // Constructor con todos los parámetros
    public Contract(int idmatch, String url) {
        this.idmatch = idmatch;
        this.url = url;
    }

    // --- Getters y Setters ---

    public int getIdmatch() {
        return idmatch;
    }

    public void setIdmatch(int idmatch) {
        this.idmatch = idmatch;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Método toString() útil para depurar e imprimir por consola
    @Override
    public String toString() {
        return "Contract{" +
                "idmatch='" + idmatch + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}