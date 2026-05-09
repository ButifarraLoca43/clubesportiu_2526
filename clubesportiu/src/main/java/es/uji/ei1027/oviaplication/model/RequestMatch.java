package es.uji.ei1027.oviaplication.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RequestMatch {
    private Integer idnumber;
    private String iduser;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private String requiredsupport;
    private String description;
    private String requirements;
    private String lifeproject;

    private String idpap;
    private EstadoMatch emparejamiento;

    public Integer getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(Integer idnumber) {
        this.idnumber = idnumber;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRequiredsupport() {
        return requiredsupport;
    }

    public void setRequiredsupport(String requiredsupport) {
        this.requiredsupport = requiredsupport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getLifeproject() {
        return lifeproject;
    }

    public void setLifeproject(String lifeproject) {
        this.lifeproject = lifeproject;
    }

    public String getIdpap() {
        return idpap;
    }

    public void setIdpap(String idpap) {
        this.idpap = idpap;
    }

    public EstadoMatch getEmparejamiento() {
        return emparejamiento;
    }

    public void setEmparejamiento(EstadoMatch emparejamiento) {
        this.emparejamiento = emparejamiento;
    }

    @Override
    public String toString() {
        return "RequestMatch{" +
                "idnumber=" + idnumber +
                ", iduser='" + iduser + '\'' +
                ", date=" + date +
                ", requiredsupport='" + requiredsupport + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", lifeproject='" + lifeproject + '\'' +
                ", idpap='" + idpap + '\'' +
                ", emparejamiento=" + emparejamiento +
                '}';
    }
}
