package gestion.model.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gestion.model.collections.Pedido;
import gestion.model.enums.EstadoPedido;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, ObjectId> {
    List<Pedido> findByMesaId(String mesaId);
    List<Pedido> findByUsuarioId(ObjectId usuarioId);
    List<Pedido> findByEstado(EstadoPedido estado);
}