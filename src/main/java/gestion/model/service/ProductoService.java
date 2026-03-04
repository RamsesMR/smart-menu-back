package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Producto;

public interface ProductoService {
    Producto findById(ObjectId idProducto);
    List<Producto> findAll();
    List<Producto> findDisponiblesByRestaurante(ObjectId restauranteId);
    Producto insertOne(Producto producto);
    Producto updateOne(Producto producto);
    int deleteOne(ObjectId idProducto);
}