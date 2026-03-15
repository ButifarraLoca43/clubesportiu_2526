package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;

public class PAP_PATI
{
    private String name;
    private String surname;
    private String email;
    private LocalDate datebirth;
    private String idnumber;
    private String address;
    private String phonenumber;
    private String experience;
    private String curriculumvitae;
    private String userpassword;
    private String username;

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
        return datebirth;
    }

    public void setDateBirth(LocalDate datebirth) {
        this.datebirth = datebirth;
    }

    public String getIdNumber() {
        return idnumber;
    }

    public void setIdNumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phonenumber;
    }

    public void setPhoneNumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCurriculumVitae() {
        return curriculumvitae;
    }

    public void setCurriculumVitae(String curriculumvitae) {
        this.curriculumvitae = curriculumvitae;
    }

    public String getUserPassword() {
        return userpassword;
    }

    public void setUserPassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "PAP_PATI{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", datebirth=" + datebirth +
                ", idnumber='" + idnumber + '\'' +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", experience='" + experience + '\'' +
                ", curriculumvitae='" + curriculumvitae + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
