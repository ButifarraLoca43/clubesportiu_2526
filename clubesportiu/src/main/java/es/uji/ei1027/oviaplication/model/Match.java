package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;

public class Match
{
    private String idnumber;
    private String iduser;
    private String idpap;
    private String idrequest;
    private LocalDate date;

    public String getIdNumber() {
        return idnumber;
    }

    public void setIdNumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getIdUser() {
        return iduser;
    }

    public void setIdUser(String iduser) {
        this.iduser = iduser;
    }

    public String getIdPAP() {
        return idpap;
    }

    public void setIdPAP(String idpap) {
        this.idpap = idpap;
    }

    public String getIdRequest() {
        return idrequest;
    }

    public void setIdRequest(String idrequest) {
        this.idrequest = idrequest;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + idnumber +
                ", date=" + date +
                ", idUser='" + iduser + '\'' +
                ", idPAP='" + idpap + '\'' +
                ", idRequest='" + idrequest + '\'' +
                '}';
    }
}
