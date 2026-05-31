package es.uji.ei1027.oviaplication.model;

public class UserDetails {
    private String userName;
    private String userPassword;
    private TipoUsuario tipoUsuario;
    private String idNumber;
    private Estado estado; // 1. Añadimos el nuevo atributo de tu Enum

    // Getters existentes
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

    // 2. NUEVO: Getter para el estado
    public Estado getEstado() {
        return estado;
    }

    // Setters existentes
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    // 3. NUEVO: Setter para el estado
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    // 4. Corrección y actualización del método toString
    @Override
    public String toString() {
        return "UserDetails{" +
                "userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", idNumber='" + idNumber + '\'' +
                ", estado=" + estado +
                '}';
    }
}