package es.uji.ei1027.oviaplication.model;

public enum Estado {
    aceptado,
    rechazado,
    pendiente;

    public static Estado fromValor(String estadoStr) {
        return Estado.valueOf(estadoStr);
    }
}
