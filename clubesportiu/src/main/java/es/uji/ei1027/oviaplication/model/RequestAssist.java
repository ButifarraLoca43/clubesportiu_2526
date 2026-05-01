package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class RequestAssist {
    private Integer idnumber;
    private String iduser;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private String requiredsupport;
    private String description;
    private String requirements;
    private String lifeproject;

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

    @Override
    public String toString() {
        return "RequestAssist{" +
                "idnumber=" + idnumber +
                ", iduser='" + iduser + '\'' +
                ", date=" + date +
                ", requiredsupport='" + requiredsupport + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", lifeproject='" + lifeproject + '\'' +
                '}';
    }
}

