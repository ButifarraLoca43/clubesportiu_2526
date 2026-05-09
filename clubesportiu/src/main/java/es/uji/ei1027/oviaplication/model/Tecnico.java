package es.uji.ei1027.oviaplication.model;

public class Tecnico {
    private String username;
    private String userpassword;


    private String idNumber;

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

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public String toString() {
        return "Tecnico{" +
                "username='" + username + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
