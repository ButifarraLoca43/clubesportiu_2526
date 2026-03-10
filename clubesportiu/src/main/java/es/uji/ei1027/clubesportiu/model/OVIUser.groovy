package es.uji.ei1027.clubesportiu.model

import java.time.LocalDate

class OVIUser {
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

    String getName() {
        return name
    }

    String getSurname() {
        return surname
    }

    LocalDate getDateBirth() {
        return dateBirth
    }

    String getIdNumber() {
        return idNumber
    }

    String getPhoneNumber() {
        return phoneNumber
    }

    String getEmail() {
        return email
    }

    String getAddress() {
        return address
    }

    DiversityType getFuncDiversity() {
        return funcDiversity
    }

    Integer getDependencyGrade() {
        return dependencyGrade
    }

    String getUserPassword() {
        return userPassword
    }

    String getUserName() {
        return userName
    }

    void setName(String name) {
        this.name = name
    }

    void setSurname(String surname) {
        this.surname = surname
    }

    void setDateBirth(LocalDate dateBirth) {
        this.dateBirth = dateBirth
    }

    void setIdNumber(String idNumber) {
        this.idNumber = idNumber
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber
    }

    void setEmail(String email) {
        this.email = email
    }

    void setAddress(String address) {
        this.address = address
    }

    void setFuncDiversity(DiversityType funcDiversity) {
        this.funcDiversity = funcDiversity
    }

    void setDependencyGrade(Integer dependencyGrade) {
        this.dependencyGrade = dependencyGrade
    }

    void setUserPassword(String userPassword) {
        this.userPassword = userPassword
    }

    void setUserName(String userName) {
        this.userName = userName
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
