package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import gestion.model.collections.Producto;

@Service
public interface ProductoService  {
	
	Producto findById (ObjectId idProducto);
	List<Producto> findAll ();
	Producto insertOne(Producto producto);
	Producto updateOne(Producto producto);
	int deleteOne(ObjectId idProducto);

}
