package gestion.model.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gestion.model.collections.Producto;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, ObjectId>  {
	
	List<Producto> findByRestauranteIdAndDisponibleTrue(ObjectId restauranteId);

	  List<Producto> findByRestauranteIdAndCategoriaIdAndDisponibleTrue(
	      ObjectId restauranteId,
	      ObjectId categoriaId
	  );

	
}
