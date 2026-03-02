package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;

import gestion.model.collections.Pedido;
import gestion.model.enums.EstadoPedido;

public interface PedidoService {
    Pedido findById(ObjectId pedidoId);
    List<Pedido> findAll();
    List<Pedido> findByMesaId(String mesaId);
    List<Pedido> findByUsuarioId(ObjectId usuarioId);
    Pedido insertOne(Pedido pedido);
    Pedido updateOne(Pedido pedido);
    Pedido cambiarEstado(ObjectId pedidoId, EstadoPedido nuevoEstado);
    int deleteOne(ObjectId pedidoId);
}