
package gestion.model.service;

import gestion.model.collections.Producto;
import gestion.model.collections.DTO.MenuSuggestion;
import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.collections.DTO.RecommendationResponse;
import gestion.model.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orquestador del flujo de recomendaciones.
 * Ya no tiene lógica propia: delega en 3 servicios especializados.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductoRepository           productoRepository;
    private final NutritionalCalculatorService calculatorService;
    private final ProductFilterService         filterService;
    private final MenuBuilderService           builderService;

    public RecommendationResponse recomendar(RecommendationRequest req) {
        if (req == null) {
            return RecommendationResponse.builder()
                    .kcalObjetivo(0).menus(List.of()).build();
        }

        // 1. Calorías objetivo
        int kcalObjetivo = (req.getKcalObjetivo() != null)
                ? req.getKcalObjetivo()
                : calculatorService.estimarKcalPorComida(req);

        // 2. Catálogo base del restaurante
        List<Producto> catalogoBase = (req.getRestauranteId() != null)
                ? productoRepository.findByRestauranteIdAndDisponibleTrue(req.getRestauranteId())
                : productoRepository.findByDisponibleTrue();

        // 3. Filtrar
        List<Producto> catalogoFiltrado = filterService.filtrar(
                catalogoBase, req.getDieta(), req.getAlergenosEvitar());

        // 4. Construir hasta 3 menús
        List<MenuSuggestion> menus = builderService.construirMenus(
                catalogoFiltrado, kcalObjetivo, req, 3);

        return RecommendationResponse.builder()
                .kcalObjetivo(kcalObjetivo)
                .menus(menus)
                .build();
    }
}