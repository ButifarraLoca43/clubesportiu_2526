package es.uji.ei1027.oviaplication.services;

import es.uji.ei1027.oviaplication.model.Nadador;
import java.util.Map;
import java.util.List;

public interface ClassificacioService {
    public Map<String, List<Nadador>> getClassificationByCountry(String prova);
}

