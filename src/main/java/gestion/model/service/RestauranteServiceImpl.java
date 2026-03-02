package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import gestion.model.collections.Restaurante;
import gestion.model.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Override
    public Restaurante findById(ObjectId restauranteId) {
        return restauranteRepository.findById(restauranteId).orElse(null);
    }

    @Override
    public List<Restaurante> findAll() {
        return restauranteRepository.findAll();
    }
}