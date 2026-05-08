package es.uji.ei1027.oviaplication.model;

public class UserDetails {
    private String userName;

    private String userPassword;

    private TipoUsuario tipoUsuario;

    private String idNumber;

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public void setIdNumber(String idNumber) {this.idNumber = idNumber;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", estado=" + tipoUsuario +
                ", idNumber" + idNumber +
                '}';
    }
}
