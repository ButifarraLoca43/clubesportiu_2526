package es.uji.ei1027.oviaplication.model;

public class Tecnico {
    private String username;
    private String userpassword;


    private String idtecnico;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public void setIdtecnico(String idtecnico) {
        this.idtecnico = idtecnico;
    }

    public String getIdtecnico() {
        return idtecnico;
    }

    @Override
    public String toString() {
        return "Tecnico{" +
                "username='" + username + '\'' +
                ", password='" + userpassword + '\'' +
                ", idtecnico='" + idtecnico + '\'' +
                '}';
    }
}
