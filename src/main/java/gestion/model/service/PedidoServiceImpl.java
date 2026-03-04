package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import gestion.model.collections.Pedido;
import gestion.model.enums.EstadoPedido;
import gestion.model.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    public Pedido findById(ObjectId pedidoId) {
        return pedidoRepository.findById(pedidoId).orElse(null);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> findByMesaId(String mesaId) {
        return pedidoRepository.findByMesaId(mesaId);
    }

    @Override
    public List<Pedido> findByUsuarioId(ObjectId usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Pedido insertOne(Pedido pedido) {
        if (pedido.getId() == null || !pedidoRepository.existsById(pedido.getId())) {
            return pedidoRepository.save(pedido);
        }
        return null;
    }

    @Override
    public Pedido updateOne(Pedido pedido) {
        if (pedidoRepository.existsById(pedido.getId())) {
            return pedidoRepository.save(pedido);
        }
        return null;
    }

    @Override
    public Pedido cambiarEstado(ObjectId pedidoId, EstadoPedido nuevoEstado) {
        return pedidoRepository.findById(pedidoId).map(pedido -> {
            pedido.setEstado(nuevoEstado);
            return pedidoRepository.save(pedido);
        }).orElse(null);
    }

    @Override
    public int deleteOne(ObjectId pedidoId) {
        if (pedidoRepository.existsById(pedidoId)) {
            pedidoRepository.deleteById(pedidoId);
            return 1;
        }
        return 0;
    }
}