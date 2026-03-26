package gestion.model.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import gestion.model.collections.Producto;
import gestion.model.collections.DTO.MenuSuggestion;
import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.collections.DTO.RecommendationResponse;
import gestion.model.enums.DietType;
import gestion.model.enums.GoalType;
import gestion.model.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductoRepository productoRepository;

    public RecommendationResponse recomendar(RecommendationRequest req) {
        if (req == null) {
            return RecommendationResponse.builder()
                    .kcalObjetivo(0)
                    .menus(List.of())
                    .build();
        }

        boolean incluirBebida = Boolean.TRUE.equals(req.getIncluirBebida());

        int kcalObjetivo = (req.getKcalObjetivo() != null)
                ? req.getKcalObjetivo()
                : estimarKcalMifflin(req);

        // 1. Obtener productos disponibles del restaurante
        List<Producto> base = (req.getRestauranteId() != null)
                ? productoRepository.findByRestauranteIdAndDisponibleTrue(req.getRestauranteId())
                : productoRepository.findByDisponibleTrue();

        // 2. Filtrar por dieta y alergenos
        List<Producto> filtrados = base.stream()
                .filter(p -> p.getKcal() != null)
                .filter(p -> cumpleDieta(p, req.getDieta()))
                .filter(p -> !contieneAlergenos(p, req.getAlergenosEvitar()))
                .filter(this::tieneTipo)
                .toList();

        // 3. Separar por tipo
        List<Producto> principales = filtrados.stream()
                .filter(p -> tieneTag(p, "PRINCIPAL"))
                .sorted(Comparator.comparingInt(p -> Math.abs(safeInt(p.getKcal()) - kcalObjetivo / 2)))
                .limit(5)
                .toList();

        List<Producto> entrantes = filtrados.stream()
                .filter(p -> tieneTag(p, "ENTRANTE"))
                .sorted(Comparator.comparingInt(p -> Math.abs(safeInt(p.getKcal()) - kcalObjetivo / 4)))
                .limit(5)
                .toList();

        List<Producto> postres = filtrados.stream()
                .filter(p -> tieneTag(p, "POSTRE"))
                .sorted(Comparator.comparingInt(p -> Math.abs(safeInt(p.getKcal()) - kcalObjetivo / 4)))
                .limit(5)
                .toList();

        List<Producto> bebidas = incluirBebida
                ? filtrados.stream()
                    .filter(p -> tieneTag(p, "BEBIDA"))
                    .limit(5)
                    .toList()
                : List.of();

        if (principales.isEmpty()) {
            return RecommendationResponse.builder()
                    .kcalObjetivo(kcalObjetivo)
                    .menus(List.of())
                    .build();
        }

        // 4. Construir máximo 3 menús combinando los mejores candidatos
        List<MenuSuggestion> menus = new ArrayList<>();
        int intentos = Math.min(3, principales.size());

        for (int i = 0; i < intentos; i++) {
            Producto principal = principales.get(i);

            // Elegir entrante distinto al principal
            Producto entrante = entrantes.stream()
                    .filter(p -> !mismoId(p, principal))
                    .findFirst()
                    .orElse(null);

            // Elegir postre distinto a los anteriores
            Producto postre = postres.stream()
                    .filter(p -> !mismoId(p, principal))
                    .filter(p -> entrante == null || !mismoId(p, entrante))
                    .findFirst()
                    .orElse(null);

            // Construir lista de productos del menú
            List<Producto> items = new ArrayList<>();
            items.add(principal);
            if (entrante != null) items.add(entrante);
            if (postre   != null) items.add(postre);

            // Añadir bebida si se requiere
            if (incluirBebida && !bebidas.isEmpty()) {
                Producto bebida = bebidas.stream()
                        .filter(p -> !mismoId(p, principal))
                        .findFirst()
                        .orElse(null);
                if (bebida != null) items.add(bebida);
            }

            menus.add(construirMenu(items, kcalObjetivo, req));
        }

        return RecommendationResponse.builder()
                .kcalObjetivo(kcalObjetivo)
                .menus(menus)
                .build();
    }

    private int estimarKcalMifflin(RecommendationRequest req) {
        double peso   = req.getPesoKg()   != null ? req.getPesoKg()   : 70.0;
        int    altura = req.getAlturaCm() != null ? req.getAlturaCm() : 170;
        int    edad   = req.getEdad()     != null ? req.getEdad()     : 30;

        // Mifflin-St Jeor c
        double tmb;
        if (req.getSexo() == gestion.model.enums.Sexo.MUJER) {
            tmb = 10 * peso + 6.25 * altura - 5.0 * edad - 161;
        } else {
            // HOMBRE o no especificado → usamos fórmula masculina por defecto
            tmb = 10 * peso + 6.25 * altura - 5.0 * edad + 5;
        }

        // TDEE: TMB × factor de actividad (moderada = 1.55)
        double tdee = tmb * 1.55;

        // Calorías por comida (3 comidas al día)
        double meal = tdee / 3.0;

        // Ajuste según objetivo
        if (req.getObjetivo() == GoalType.PERDER_PESO)    meal *= 0.80;
        else if (req.getObjetivo() == GoalType.GANAR_MUSCULO) meal *= 1.15;

        return (int) Math.round(meal);
    }

    // Scoring 
    private double score(MenuSuggestion m, int kcalObjetivo, RecommendationRequest req) {
        double diff  = Math.abs(m.getKcalTotal() - kcalObjetivo);
        double bonus = 0;

        if (req.getObjetivo() == GoalType.PERDER_PESO) {
            bonus -= bonusTag(m, "LIGERO") * 40;
            if (m.getKcalTotal() > kcalObjetivo) diff += 150;
        } else if (req.getObjetivo() == GoalType.GANAR_MUSCULO) {
            bonus -= bonusTag(m, "ALTO_PROTEINA") * 40;
            bonus -= bonusTag(m, "ALTA_ENERGIA")  * 30;
            if (m.getKcalTotal() < kcalObjetivo)  diff += 120;
        } else {
            bonus -= bonusTag(m, "EQUILIBRADO") * 30;
        }
        return diff + bonus;
    }

    private int bonusTag(MenuSuggestion m, String tag) {
        return (int) m.getProductos().stream().filter(p -> tieneTag(p, tag)).count();
    }

    // Construcción de un menú
    private MenuSuggestion construirMenu(List<Producto> productos,
                                         int kcalObjetivo,
                                         RecommendationRequest req) {
        int        kcalTotal = productos.stream().mapToInt(p -> safeInt(p.getKcal())).sum();
        BigDecimal prot  = sumBD(productos, "P");
        BigDecimal grasa = sumBD(productos, "G");
        BigDecimal carb  = sumBD(productos, "C");

        String reason = "Menú adaptado a "
                + (req.getDieta()    == null ? "NORMAL"   : req.getDieta().name())
                + " y objetivo "
                + (req.getObjetivo() == null ? "MANTENER" : req.getObjetivo().name())
                + ". Objetivo: " + kcalObjetivo + " kcal, menú: " + kcalTotal + " kcal.";

        return MenuSuggestion.builder()
                .tipo("IA")
                .kcalTotal(kcalTotal)
                .proteTotal(prot)
                .grasasTotal(grasa)
                .carbTotal(carb)
                .productos(productos)
                .reason(reason)
                .build();
    }

    private BigDecimal sumBD(List<Producto> productos, String which) {
        BigDecimal total = BigDecimal.ZERO;
        for (Producto p : productos) {
            BigDecimal v = switch (which) {
                case "P" -> p.getProteinas()     != null ? p.getProteinas()     : BigDecimal.ZERO;
                case "G" -> p.getGrasas()        != null ? p.getGrasas()        : BigDecimal.ZERO;
                case "C" -> p.getCarbohidratos() != null ? p.getCarbohidratos() : BigDecimal.ZERO;
                default  -> BigDecimal.ZERO;
            };
            total = total.add(v);
        }
        return total;
    }

    private int safeInt(Integer n) { return n == null ? 0 : n; }

    // Validación menú 
    private boolean menuValido(List<Producto> items, boolean incluirBebida, boolean incluyeBebidaEnLista) {
        long numPrincipales = items.stream().filter(p -> tieneTag(p, "PRINCIPAL")).count();
        long numPostres     = items.stream().filter(p -> tieneTag(p, "POSTRE")).count();
        long numBebidas     = items.stream().filter(p -> tieneTag(p, "BEBIDA")).count();

        if (numPrincipales < 1) return false;
        if (numPostres     > 1) return false;
        if (!incluirBebida && numBebidas > 0) return false;
        if (incluirBebida  && numBebidas > 1) return false;
        if (incluyeBebidaEnLista && numBebidas != 1) return false;
        return true;
    }

    // Filtros
    private boolean tieneTipo(Producto p) {
        return tieneTag(p, "ENTRANTE") || tieneTag(p, "PRINCIPAL")
            || tieneTag(p, "POSTRE")   || tieneTag(p, "BEBIDA");
    }

    private boolean tieneTag(Producto p, String tag) {
        return p.getTags() != null && p.getTags().contains(tag);
    }

    private boolean contieneAlergenos(Producto p, List<String> evitar) {
        if (evitar == null || evitar.isEmpty()) return false;
        if (p.getAlergenos() == null || p.getAlergenos().isEmpty()) return false;
        return p.getAlergenos().stream().anyMatch(evitar::contains);
    }

    private boolean cumpleDieta(Producto p, DietType dieta) {
        if (dieta == null || dieta == DietType.NORMAL) return true;
        if (p.getTags() == null) return false;
        if (dieta == DietType.VEGANA)       return p.getTags().contains("VEGANO");
        if (dieta == DietType.VEGETARIANA)  return p.getTags().contains("VEGETARIANO") || p.getTags().contains("VEGANO");
        return true;
    }

    private boolean sinRepetidos(List<Producto> productos) {
        return productos.stream()
                .map(p -> p.getId() == null ? null : p.getId().toHexString())
                .filter(id -> id != null)
                .distinct()
                .count() == productos.size();
    }

    // Combinadores
    private List<Producto> limitar(List<Producto> lista, int n) {
        if (lista == null) return List.of();
        return lista.size() <= n ? lista : lista.subList(0, n);
    }

    private List<Producto> combinar2(List<Producto> entrantes, List<Producto> principales) {
        List<Producto> out = new ArrayList<>(entrantes);
        out.addAll(principales);
        return limitar(out, 20);
    }

    private List<Producto> combinar3(List<Producto> entrantes, List<Producto> postres, List<Producto> principales) {
        List<Producto> out = new ArrayList<>(entrantes);
        out.addAll(postres);
        out.addAll(principales);
        return limitar(out, 25);
    }
    
    private boolean mismoId(Producto a, Producto b) {
        if (a == null || b == null) return false;
        if (a.getId() == null || b.getId() == null) return false;
        return a.getId().equals(b.getId());
    }
}
