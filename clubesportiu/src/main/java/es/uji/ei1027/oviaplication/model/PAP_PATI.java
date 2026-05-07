package es.uji.ei1027.oviaplication.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PAP_PATI
{
    private String name;
    private String surname;
    private String email;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateBirth;
    private String idNumber;
    private String address;
    private String phoneNumber;
    private String experience;
    private String curriculumVitae;
    private String userPassword;
    private String userName;
    private Estado estado;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(LocalDate datebirth) {
        this.dateBirth = datebirth;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idnumber) {
        this.idNumber = idnumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phoneNumber = phonenumber;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCurriculumVitae() {
        return curriculumVitae;
    }

    public void setCurriculumVitae(String curriculumvitae) {
        this.curriculumVitae = curriculumvitae;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userpassword) {
        this.userPassword = userpassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "PAP_PATI{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", datebirth=" + dateBirth +
                ", idnumber='" + idNumber + '\'' +
                ", address='" + address + '\'' +
                ", phonenumber='" + phoneNumber + '\'' +
                ", experience='" + experience + '\'' +
                ", curriculumvitae='" + curriculumVitae + '\'' +
                ", userpassword='" + userPassword + '\'' +
                ", username='" + userName + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }

}
