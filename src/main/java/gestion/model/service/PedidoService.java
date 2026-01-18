package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Pedido;

public interface PedidoService {
	
	Pedido findById(ObjectId pedidoId);
	List<Pedido> findAll();
	Pedido insertOne(Pedido pedido);
	Pedido updateOne(Pedido pedido);
	int deleteOne(ObjectId pedidoId);

}
