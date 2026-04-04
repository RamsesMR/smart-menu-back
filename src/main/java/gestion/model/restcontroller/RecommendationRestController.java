package gestion.model.restcontroller;

import gestion.model.collections.DTO.RecommendationRequest;
import gestion.model.collections.DTO.RecommendationResponse;
import gestion.model.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationRestController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> recomendar(
            @Valid @RequestBody RecommendationRequest req) {
        return ResponseEntity.ok(recommendationService.recomendar(req));
    }
}