package es.uji.ei1027.clubesportiu.services;

import es.uji.ei1027.clubesportiu.model.Nadador;
import java.util.Map;
import java.util.List;

public interface ClassificacioService {
    public Map<String, List<Nadador>> getClassificationByCountry(String prova);
}

