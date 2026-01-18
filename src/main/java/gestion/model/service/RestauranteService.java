package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Restaurante;


public interface RestauranteService {
	
	Restaurante findById(ObjectId restauranteId);
	List<Restaurante> findAll();

}
