package gestion.model.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gestion.model.collections.Categoria;

@Repository
public interface CategoriaRepository extends MongoRepository<Categoria, ObjectId> {

}
