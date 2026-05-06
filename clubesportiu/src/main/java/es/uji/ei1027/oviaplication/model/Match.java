package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;

public class Match
{
    private Integer idnumber;
    private String iduser;
    private String idpap;
    private Integer idrequest;
    private LocalDate date;
    private EstadoMatch emparejamiento;

    public Integer getIdNumber() {
        return idnumber;
    }

    public void setIdNumber(Integer idnumber) {
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

    public Integer getIdRequest() {
        return idrequest;
    }

    public void setIdRequest(Integer idrequest) {
        this.idrequest = idrequest;
    }

    public EstadoMatch getEstado() {
        return emparejamiento;
    }

    public void setEstado(EstadoMatch estado) {
        this.emparejamiento = estado;
    }

    @Override
    public String toString() {
        return "Match{" +
                "idnumber='" + idnumber + '\'' +
                ", iduser='" + iduser + '\'' +
                ", idpap='" + idpap + '\'' +
                ", idrequest='" + idrequest + '\'' +
                ", date=" + date +
                ", estado=" + emparejamiento +
                '}';
    }
}
