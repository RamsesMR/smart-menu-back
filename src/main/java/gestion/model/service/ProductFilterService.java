package gestion.model.service;

import gestion.model.collections.Producto;
import gestion.model.enums.DietType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Filtra productos por dieta, alérgenos y tipo de plato.
 * Extraído de RecommendationService para respetar el SRP.
 */
@Service
public class ProductFilterService {

    public List<Producto> filtrar(
            List<Producto> productos,
            DietType dieta,
            List<String> alergenosEvitar) {

        return productos.stream()
                .filter(p -> p.getKcal() != null)
                .filter(p -> cumpleDieta(p, dieta))
                .filter(p -> !contieneAlergenos(p, alergenosEvitar))
                .filter(this::esTipoReconocido)
                .toList();
    }

    public boolean esTipoReconocido(Producto p) {
        return tieneTag(p, "ENTRANTE")  || tieneTag(p, "PRINCIPAL")
            || tieneTag(p, "POSTRE")    || tieneTag(p, "BEBIDA");
    }

    public boolean cumpleDieta(Producto p, DietType dieta) {
        if (dieta == null || dieta == DietType.NORMAL) return true;
        if (p.getTags() == null) return false;
        return switch (dieta) {
            case VEGANA       -> p.getTags().contains("VEGANO");
            case VEGETARIANA  -> p.getTags().contains("VEGETARIANO")
                                 || p.getTags().contains("VEGANO");
            default           -> true;
        };
    }

    public boolean contieneAlergenos(Producto p, List<String> evitar) {
        if (evitar == null || evitar.isEmpty()) return false;
        if (p.getAlergenos() == null || p.getAlergenos().isEmpty()) return false;
        return p.getAlergenos().stream().anyMatch(evitar::contains);
    }

    public boolean tieneTag(Producto p, String tag) {
        return p.getTags() != null && p.getTags().contains(tag);
    }
}