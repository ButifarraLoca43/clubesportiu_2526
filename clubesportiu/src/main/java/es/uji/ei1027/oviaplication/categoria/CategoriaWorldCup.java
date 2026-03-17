package es.uji.ei1027.oviaplication.categoria;

import org.springframework.stereotype.Component;

@Component
public class CategoriaWorldCup implements Categoria {
    @Override
    public String getCategoria(int edat) {
        String categoria;
        if (edat<18) {
            categoria = "JUNIOR";
        }
        else {
            categoria = "SENIOR";
        }
        return categoria;
    }

}


