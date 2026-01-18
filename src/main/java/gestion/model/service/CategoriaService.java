package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Categoria;

public interface CategoriaService {
	
	Categoria findById(ObjectId categoriaId);
	List<Categoria> findAll();
	Categoria insertOne(Categoria categoria);
	Categoria updateOne(Categoria categoria);
	int deleteOne(ObjectId categoriaId);
	
	

}
