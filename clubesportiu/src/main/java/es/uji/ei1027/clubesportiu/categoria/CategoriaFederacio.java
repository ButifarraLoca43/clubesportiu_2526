package es.uji.ei1027.clubesportiu.categoria;

import org.springframework.stereotype.Component;

@Component
public class CategoriaFederacio implements Categoria {

    @Override
    public String getCategoria(int edat) {
        String categoria;
        if (edat<18) {
            categoria = "JUNIOR";
        }
        else {
            if (edat<21) {
                categoria = "ABSOLUT";
            }
            else {
                categoria = "SENIOR";
            }
        }
        return categoria;
    }
}

