package gestion.model.service;

import gestion.model.collections.Producto;
import gestion.model.collections.DTO.MenuSuggestion;
import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.enums.GoalType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Construye y puntúa las propuestas de menú.
 * Extraído de RecommendationService para respetar el SRP.
 */
@Service
@RequiredArgsConstructor
public class MenuBuilderService {

    private final ProductFilterService filterService;

    public List<MenuSuggestion> construirMenus(
            List<Producto> filtrados,
            int kcalObjetivo,
            RecommendationRequest req,
            int maxMenus) {

        boolean incluirBebida = Boolean.TRUE.equals(req.getIncluirBebida());

        List<Producto> principales = candidatos(filtrados, "PRINCIPAL", kcalObjetivo / 2);
        List<Producto> entrantes   = candidatos(filtrados, "ENTRANTE",  kcalObjetivo / 4);
        List<Producto> postres     = candidatos(filtrados, "POSTRE",    kcalObjetivo / 4);
        List<Producto> bebidas     = incluirBebida
                ? filtrados.stream()
                    .filter(p -> filterService.tieneTag(p, "BEBIDA"))
                    .limit(5).toList()
                : List.of();

        if (principales.isEmpty()) return List.of();

        List<MenuSuggestion> propuestas = new ArrayList<>();
        int intentos = Math.min(maxMenus, principales.size());

        for (int i = 0; i < intentos; i++) {
            Producto principal = principales.get(i);

            Producto entrante = entrantes.stream()
                    .filter(p -> !mismoId(p, principal))
                    .findFirst().orElse(null);

            Producto postre = postres.stream()
                    .filter(p -> !mismoId(p, principal))
                    .filter(p -> entrante == null || !mismoId(p, entrante))
                    .findFirst().orElse(null);

            List<Producto> items = new ArrayList<>();
            items.add(principal);
            if (entrante != null) items.add(entrante);
            if (postre   != null) items.add(postre);

            if (incluirBebida && !bebidas.isEmpty()) {
                bebidas.stream()
                        .filter(p -> !mismoId(p, principal))
                        .findFirst()
                        .ifPresent(items::add);
            }

            propuestas.add(ensamblarMenu(items, kcalObjetivo, req));
        }

        propuestas.sort(Comparator.comparingDouble(
                m -> puntuacion(m, kcalObjetivo, req)));
        return propuestas;
    }

    // ── Privados ─────────────────────────────────────────────────────────

    private MenuSuggestion ensamblarMenu(
            List<Producto> productos,
            int kcalObjetivo,
            RecommendationRequest req) {

        int        kcalTotal = productos.stream()
                                        .mapToInt(p -> seguro(p.getKcal())).sum();
        BigDecimal prot      = sumarMacro(productos, "P");
        BigDecimal grasa     = sumarMacro(productos, "G");
        BigDecimal carb      = sumarMacro(productos, "C");

        String razon = String.format(
            "Menú adaptado a %s y objetivo %s. Objetivo: %d kcal, menú: %d kcal.",
            req.getDieta()    == null ? "NORMAL"   : req.getDieta().name(),
            req.getObjetivo() == null ? "MANTENER" : req.getObjetivo().name(),
            kcalObjetivo, kcalTotal
        );

        return MenuSuggestion.builder()
                .tipo("IA")
                .kcalTotal(kcalTotal)
                .proteTotal(prot)
                .grasasTotal(grasa)
                .carbTotal(carb)
                .productos(productos)
                .reason(razon)
                .build();
    }

    private double puntuacion(MenuSuggestion m, int kcalObjetivo,
                               RecommendationRequest req) {
        double diff  = Math.abs(m.getKcalTotal() - kcalObjetivo);
        double bonus = 0;

        if (req.getObjetivo() == GoalType.PERDER_PESO) {
            bonus -= contarTag(m, "LIGERO") * 40;
            if (m.getKcalTotal() > kcalObjetivo) diff += 150;
        } else if (req.getObjetivo() == GoalType.GANAR_MUSCULO) {
            bonus -= contarTag(m, "ALTO_PROTEINA") * 40;
            bonus -= contarTag(m, "ALTA_ENERGIA")  * 30;
            if (m.getKcalTotal() < kcalObjetivo)   diff += 120;
        } else {
            bonus -= contarTag(m, "EQUILIBRADO") * 30;
        }
        return diff + bonus;
    }

    private long contarTag(MenuSuggestion m, String tag) {
        return m.getProductos().stream()
                .filter(p -> filterService.tieneTag(p, tag)).count();
    }

    private List<Producto> candidatos(
            List<Producto> productos, String tipo, int kcalRef) {
        return productos.stream()
                .filter(p -> filterService.tieneTag(p, tipo))
                .sorted(Comparator.comparingInt(
                        p -> Math.abs(seguro(p.getKcal()) - kcalRef)))
                .limit(5).toList();
    }

    private BigDecimal sumarMacro(List<Producto> productos, String macro) {
        BigDecimal total = BigDecimal.ZERO;
        for (Producto p : productos) {
            BigDecimal v = switch (macro) {
                case "P" -> p.getProteinas()     != null ? p.getProteinas()     : BigDecimal.ZERO;
                case "G" -> p.getGrasas()        != null ? p.getGrasas()        : BigDecimal.ZERO;
                case "C" -> p.getCarbohidratos() != null ? p.getCarbohidratos() : BigDecimal.ZERO;
                default  -> BigDecimal.ZERO;
            };
            total = total.add(v);
        }
        return total;
    }

    private int seguro(Integer n) { return n == null ? 0 : n; }

    private boolean mismoId(Producto a, Producto b) {
        if (a == null || b == null) return false;
        if (a.getId() == null || b.getId() == null) return false;
        return a.getId().equals(b.getId());
    }
}