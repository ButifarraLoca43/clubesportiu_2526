package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;

public class OVIUser {
    private String name;
    private String surname;
    private LocalDate dateBirth;
    private String idNumber;
    private String phoneNumber;
    private String email;
    private String address;
    private DiversityType funcDiversity;
    private Integer dependencyGrade;
    private String userPassword;
    private String userName;

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public DiversityType getFuncDiversity() {
        return funcDiversity;
    }

    public Integer getDependencyGrade() {
        return dependencyGrade;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateBirth(LocalDate dateBirth) {
        this.dateBirth = dateBirth;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFuncDiversity(DiversityType funcDiversity) {
        this.funcDiversity = funcDiversity;
    }

    public void setDependencyGrade(Integer dependencyGrade) {
        this.dependencyGrade = dependencyGrade;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "OVIUser{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", dateBirth=" + dateBirth +
                ", idNumber='" + idNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", funcDiversity=" + funcDiversity +
                ", dependencyGrade=" + dependencyGrade +
                ", userPassword='" + userPassword + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}