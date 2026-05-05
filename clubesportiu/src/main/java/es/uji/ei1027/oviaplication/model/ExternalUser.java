package es.uji.ei1027.oviaplication.model;

public class ExternalUser {
    private String idnumber;
    private String name;
    private String surname;
    private String email;
    private String phonenumber;

    // Genera aquí los Getters y Setters vacíos
    public ExternalUser() {}

    public String getIdnumber() { return idnumber; }
    public void setIdnumber(String idnumber) { this.idnumber = idnumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhonenumber() { return phonenumber; }
    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; }
}