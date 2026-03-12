package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class Match
{
    private int id;
    private LocalDate date;
    private String idUser;
    private String idPAP;
    private String idRequest;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPAP() {
        return idPAP;
    }

    public void setIdPAP(String idPAP) {
        this.idPAP = idPAP;
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", date=" + date +
                ", idUser='" + idUser + '\'' +
                ", idPAP='" + idPAP + '\'' +
                ", idRequest='" + idRequest + '\'' +
                '}';
    }
}
