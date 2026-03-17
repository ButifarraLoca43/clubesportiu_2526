package es.uji.ei1027.oviaplication.model;

import java.time.LocalDate;

public class Prova {
    private String nom;
    private String descripcio;
    private String tipus;
    private LocalDate date;

    public String getNom() {
        return nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public String getTipus() {
        return tipus;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Prova{" +
                "nom='" + nom + '\'' +
                ", descripcio='" + descripcio + '\'' +
                ", tipus='" + tipus + '\'' +
                ", date=" + date +
                '}';
    }
}
