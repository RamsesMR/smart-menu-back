package gestion.model.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gestion.model.collections.Producto;
import gestion.model.collections.DTO.MenuSuggestion;
import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.collections.DTO.RecommendationResponse;
import gestion.model.enums.DietType;
import gestion.model.enums.GoalType;
import gestion.model.repository.ProductoRepository;

@Service
public class RecommendationService {

  @Autowired
  private ProductoRepository productoRepository;

  public RecommendationResponse recomendar(RecommendationRequest req) {
    if (req == null || req.getRestauranteId() == null) {
      return RecommendationResponse.builder().kcalObjetivo(0).menus(List.of()).build();
    }

    boolean incluirBebida = Boolean.TRUE.equals(req.getIncluirBebida());

    int kcalObjetivo = (req.getKcalObjetivo() != null)
        ? req.getKcalObjetivo()
        : estimarKcal(req);

    List<Producto> base = productoRepository
        .findByRestauranteIdAndDisponibleTrue(req.getRestauranteId());

    //  modo mixto: kcal obligatoria, macros opcionales
    List<Producto> filtrados = base.stream()
        .filter(p -> p.getKcal() != null)
        .filter(p -> cumpleDieta(p, req.getDieta()))
        .filter(p -> !contieneAlergenos(p, req.getAlergenosEvitar()))
        .filter(p -> tieneTipo(p)) // debe tener ENTRANTE/PRINCIPAL/POSTRE/BEBIDA
        .toList();

    List<Producto> principales = filtrados.stream().filter(p -> tieneTag(p, "PRINCIPAL")).toList();
    List<Producto> entrantes  = filtrados.stream().filter(p -> tieneTag(p, "ENTRANTE")).toList();
    List<Producto> postres    = filtrados.stream().filter(p -> tieneTag(p, "POSTRE")).toList();
    List<Producto> bebidas    = incluirBebida
        ? filtrados.stream().filter(p -> tieneTag(p, "BEBIDA")).toList()
        : List.of();

    // Si no hay principales, no hay menús “con sentido”
    if (principales.isEmpty()) {
      return RecommendationResponse.builder()
          .kcalObjetivo(kcalObjetivo)
          .menus(List.of())
          .build();
    }

    // Limitar para no explotar combinaciones
    principales = limitar(principales, 18);
    entrantes   = limitar(entrantes, 18);
    postres     = limitar(postres, 18);
    bebidas     = limitar(bebidas, 18);

    List<MenuSuggestion> candidatos = new ArrayList<>();

    // Generamos menús de 3 
    // Base: siempre 1 principal
    // Luego combinamos con (entrante/postre/otro principal) y bebida si se pide.

    // Caso A: principal + entrante + (entrante/principal/postre)
    for (Producto p : principales) {
      // segundo: entrante o principal
      for (Producto s : combinar2(entrantes, principales)) {

        // tercer: entrante o postre o principal
        for (Producto t : combinar3(entrantes, postres, principales)) {
        	List<Producto> menu = List.of(p, s, t);

        	if (!sinRepetidos(menu)) continue;
        	if (!menuValido(menu, incluirBebida, false)) continue;

        	if (incluirBebida) {
        	  for (Producto b : bebidas) {
        	    List<Producto> menuConBebida = List.of(p, s, t, b);

        	    if (!sinRepetidos(menuConBebida)) continue;
        	    if (!menuValido(menuConBebida, true, true)) continue;

        	    candidatos.add(construirMenu("IA_LIGHT", menuConBebida, kcalObjetivo, req));
        	  }
        	} else {
        	  candidatos.add(construirMenu("IA_LIGHT", menu, kcalObjetivo, req));
        	}


        }
      }
    }

    // Orden por score (cercanía kcal + bonus tags)
    candidatos.sort(Comparator.comparingDouble(m -> score(m, kcalObjetivo, req)));

    // Top 3
    List<MenuSuggestion> top3 = candidatos.stream().limit(3).toList();

    return RecommendationResponse.builder()
        .kcalObjetivo(kcalObjetivo)
        .menus(top3)
        .build();
  }

  // Reglas
  private boolean menuValido(List<Producto> items, boolean incluirBebida, boolean incluyeBebidaEnLista) {
    long numPrincipales = items.stream().filter(p -> tieneTag(p, "PRINCIPAL")).count();
    long numPostres = items.stream().filter(p -> tieneTag(p, "POSTRE")).count();
    long numBebidas = items.stream().filter(p -> tieneTag(p, "BEBIDA")).count();

    if (numPrincipales < 1) return false;
    if (numPostres > 1) return false;

    if (!incluirBebida) {
      // si no pidió bebida, no aceptamos bebida dentro
      if (numBebidas > 0) return false;
    } else {
      // si pidió bebida, máximo 1
      if (numBebidas > 1) return false;
      // si estamos validando la lista “con bebida”, debe tener 1 bebida
      if (incluyeBebidaEnLista && numBebidas != 1) return false;
    }

    return true;
  }

  private boolean tieneTipo(Producto p) {
    return tieneTag(p, "ENTRANTE") || tieneTag(p, "PRINCIPAL") || tieneTag(p, "POSTRE") || tieneTag(p, "BEBIDA");
  }

  // ---------- Scoring ----------
  private double score(MenuSuggestion m, int kcalObjetivo, RecommendationRequest req) {
    double diff = Math.abs(m.getKcalTotal() - kcalObjetivo);

    double bonus = 0;
    // bonus por objetivo
    if (req.getObjetivo() == GoalType.PERDER) {
      bonus -= bonusTag(m, "LIGERO") * 40;
      if (m.getKcalTotal() > kcalObjetivo) diff += 150;
    } else if (req.getObjetivo() == GoalType.GANAR) {
      bonus -= bonusTag(m, "ALTO_PROTEINA") * 40;
      bonus -= bonusTag(m, "ALTA_ENERGIA") * 30;
      if (m.getKcalTotal() < kcalObjetivo) diff += 120;
    } else { // MANTENER
      bonus -= bonusTag(m, "EQUILIBRADO") * 30;
    }

    return diff + bonus;
  }

  private int bonusTag(MenuSuggestion m, String tag) {
    return (int) m.getProductos().stream().filter(p -> tieneTag(p, tag)).count();
  }

  // ---------- Construcción menú ----------
  private MenuSuggestion construirMenu(String tipo, List<Producto> productos, int kcalObjetivo, RecommendationRequest req) {
    int kcalTotal = productos.stream().mapToInt(p -> safeInt(p.getKcal())).sum();

    BigDecimal prot = sumBD(productos, "P");
    BigDecimal grasa = sumBD(productos, "G");
    BigDecimal carb = sumBD(productos, "C");

    String reason = "Menú adaptado a " + (req.getDieta() == null ? "NORMAL" : req.getDieta().name())
        + " y objetivo " + (req.getObjetivo() == null ? "MANTENER" : req.getObjetivo().name())
        + ". Cercano a " + kcalObjetivo + " kcal.";

    return MenuSuggestion.builder()
        .tipo(tipo)
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
      BigDecimal v = BigDecimal.ZERO;
      if ("P".equals(which) && p.getProteinas() != null) v = p.getProteinas();
      if ("G".equals(which) && p.getGrasas() != null) v = p.getGrasas();
      if ("C".equals(which) && p.getCarbohidratos() != null) v = p.getCarbohidratos();
      total = total.add(v);
    }
    return total;
  }

  private int safeInt(Integer n) { return n == null ? 0 : n; }

  // ---------- Filtros ----------
  private boolean contieneAlergenos(Producto p, List<String> evitar) {
    if (evitar == null || evitar.isEmpty()) return false;
    if (p.getAlergenos() == null || p.getAlergenos().isEmpty()) return false;
    return p.getAlergenos().stream().anyMatch(evitar::contains);
  }

  private boolean cumpleDieta(Producto p, DietType dieta) {
    if (dieta == null || dieta == DietType.NORMAL) return true;
    if (p.getTags() == null) return false;

    if (dieta == DietType.VEGANA) {
      return p.getTags().contains("VEGANO");
    }
    if (dieta == DietType.VEGETARIANA) {
      return p.getTags().contains("VEGETARIANO") || p.getTags().contains("VEGANO");
    }
    return true;
  }

  private boolean tieneTag(Producto p, String tag) {
    return p.getTags() != null && p.getTags().contains(tag);
  }

  // Helpers combinaciones 
  private List<Producto> limitar(List<Producto> lista, int n) {
    if (lista == null) return List.of();
    return lista.size() <= n ? lista : lista.subList(0, n);
  }

  private List<Producto> combinar2(List<Producto> entrantes, List<Producto> principales) {
    // mezcla entrantes+principales para segunda posición
    List<Producto> out = new ArrayList<>();
    out.addAll(entrantes);
    out.addAll(principales);
    return limitar(out, 20);
  }

  private List<Producto> combinar3(List<Producto> entrantes, List<Producto> postres, List<Producto> principales) {
    // mezcla entrantes+postres+principales para tercera posición
    List<Producto> out = new ArrayList<>();
    out.addAll(entrantes);
    out.addAll(postres);
    out.addAll(principales);
    return limitar(out, 25);
  }

  // kcal estimada simple
  private int estimarKcal(RecommendationRequest req) {
    double peso = (req.getPesoKg() == null) ? 70 : req.getPesoKg();
    int base = (int) (peso * 30);

    if (req.getObjetivo() == GoalType.PERDER) return (int) (base * 0.85);
    if (req.getObjetivo() == GoalType.GANAR) return (int) (base * 1.10);
    return base;
  }
  
  private boolean sinRepetidos(List<Producto> productos) {
	  return productos.stream()
	      .map(p -> p.getId() == null ? null : p.getId().toHexString())
	      .filter(id -> id != null)
	      .distinct()
	      .count() == productos.size();
	}


}

