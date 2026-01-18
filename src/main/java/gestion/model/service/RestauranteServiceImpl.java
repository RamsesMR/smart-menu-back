package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gestion.model.collections.Restaurante;
import gestion.model.repository.RestauranteRepository;

@Service
public class RestauranteServiceImpl implements RestauranteService {
	
	@Autowired
	private RestauranteRepository restauranteRepository;

	@Override
	public Restaurante findById(ObjectId restauranteId) {
		// TODO Auto-generated method stub
		return restauranteRepository.findById(restauranteId).orElse(null);
	}

	@Override
	public List<Restaurante> findAll() {
		// TODO Auto-generated method stub
		return restauranteRepository.findAll();
	}
	

}
