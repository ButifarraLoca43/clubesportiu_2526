package es.uji.ei1027.oviaplication.model;

public enum EstadoMatch {
    PENDIENTE_OVI("pendiente_OVI"),
    PENDIENTE_PAP("pendiente_PAP"),
    ACEPTADO_OVI("aceptado_OVI"),
    ACEPTADO_PAP("aceptado_PAP"),
    RECHAZA_OVI("rechaza_OVI"),
    RECHAZA_PAP("rechaza_PAP");

    private final String valor;

    EstadoMatch(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoMatch fromValor(String valor) {
        for (EstadoMatch estado : EstadoMatch.values()) {
            if (estado.valor.equals(valor)) {
                return estado;
            }
        }
        return null;
    }
}