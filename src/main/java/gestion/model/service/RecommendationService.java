package gestion.model.service;

import gestion.model.collections.Producto;
import gestion.model.collections.DTO.MenuSuggestion;
import gestion.model.collections.DTO.MenuSuggestionDto;
import gestion.model.collections.DTO.ProductoRecommendationDto;
import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.collections.DTO.RecommendationResponse;
import gestion.model.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductoRepository productoRepository;
    private final NutritionalCalculatorService calculatorService;
    private final ProductFilterService filterService;
    private final MenuBuilderService builderService;

    public RecommendationResponse recomendar(RecommendationRequest req) {
        if (req == null) {
            return RecommendationResponse.builder()
                    .kcalObjetivo(0)
                    .menus(List.of())
                    .build();
        }

        int kcalObjetivo = (req.getKcalObjetivo() != null)
                ? req.getKcalObjetivo()
                : calculatorService.estimarKcalPorComida(req);

        List<Producto> catalogoBase = (req.getRestauranteId() != null)
                ? productoRepository.findByRestauranteIdAndDisponibleTrue(req.getRestauranteId())
                : productoRepository.findByDisponibleTrue();

        List<Producto> catalogoFiltrado = filterService.filtrar(
                catalogoBase,
                req.getDieta(),
                req.getAlergenosEvitar()
        );

        List<MenuSuggestion> menusBrutos = builderService.construirMenus(
                catalogoFiltrado,
                kcalObjetivo,
                req,
                3
        );

        List<MenuSuggestionDto> menusDto = menusBrutos.stream()
                .map(this::toMenuSuggestionDto)
                .toList();

        return RecommendationResponse.builder()
                .kcalObjetivo(kcalObjetivo)
                .menus(menusDto)
                .build();
    }

    private MenuSuggestionDto toMenuSuggestionDto(MenuSuggestion menu) {
        return MenuSuggestionDto.builder()
                .tipo(menu.getTipo())
                .kcalTotal(menu.getKcalTotal())
                .proteTotal(menu.getProteTotal())
                .grasasTotal(menu.getGrasasTotal())
                .carbTotal(menu.getCarbTotal())
                .reason(menu.getReason())
                .productos(
                        menu.getProductos() == null
                                ? List.of()
                                : menu.getProductos().stream()
                                    .map(this::toProductoRecommendationDto)
                                    .toList()
                )
                .build();
    }

    private ProductoRecommendationDto toProductoRecommendationDto(Producto producto) {
        return ProductoRecommendationDto.builder()
                .id(producto.getId() != null ? producto.getId().toHexString() : null)
                .categoriaId(producto.getCategoriaId() != null ? producto.getCategoriaId().toHexString() : null)
                .restauranteId(producto.getRestauranteId() != null ? producto.getRestauranteId().toHexString() : null)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .tipoIva(producto.getTipoIva())
                .importeIva(producto.getImporteIva())
                .precioConIva(producto.getPrecioConIva())
                .imagen(producto.getImagen())
                .disponible(producto.isDisponible())
                .tags(producto.getTags())
                .alergenos(producto.getAlergenos())
                .kcal(producto.getKcal())
                .proteinas(producto.getProteinas())
                .grasas(producto.getGrasas())
                .carbohidratos(producto.getCarbohidratos())
                .build();
    }
}