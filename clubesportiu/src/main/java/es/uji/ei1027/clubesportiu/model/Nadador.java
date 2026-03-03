package es.uji.ei1027.clubesportiu.model;

public class Nadador {
    private String nom;
    private String numFederat;
    private String pais;
    private int edat;
    private String genere;

    public String getGenere() {
        return genere;
    }

    public String getNumFederat() {
        return numFederat;
    }

    public String getNom() {
        return nom;
    }

    public String getPais() {
        return pais;
    }

    public int getEdat() {
        return edat;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNumFederat(String numFederat) {
        this.numFederat = numFederat;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setEdat(int edat) {
        this.edat = edat;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    @Override
    public String toString() {
        return "Nadador{" +
                "nom='" + nom + '\'' +
                ", numFederat='" + numFederat + '\'' +
                ", pais='" + pais + '\'' +
                ", edat=" + edat +
                ", genere='" + genere + '\'' +
                '}';
    }


}

