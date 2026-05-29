package es.uji.ei1027.oviaplication.model;

public enum TipoActividad {
    formacion,
    divulgacion;

    public static TipoActividad fromValor(String estadoStr) {
        return TipoActividad.valueOf(estadoStr);
    }
}

