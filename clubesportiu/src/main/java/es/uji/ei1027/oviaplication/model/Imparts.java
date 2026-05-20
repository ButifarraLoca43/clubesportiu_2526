package es.uji.ei1027.oviaplication.model;

public class Imparts {
    private Integer idNumber;
    private String idInstructor;
    private Integer idActivity;
    private Estado estado;

    public Imparts(){}

    public Integer getIdNumber() { return idNumber; }
    public void setIdNumber(Integer idNumber) { this.idNumber = idNumber; }

    public String getIdInstructor() { return idInstructor; }
    public void setIdInstructor(String idInstructor) { this.idInstructor = idInstructor; }


    public Integer getIdActivity() { return idActivity; }
    public void setIdActivity(Integer idActivity) { this.idActivity = idActivity; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
