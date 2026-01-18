package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gestion.model.collections.Pedido;
import gestion.model.repository.PedidoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@Override
	public Pedido findById(ObjectId pedidoId) {
		// TODO Auto-generated method stub
		return pedidoRepository.findById(pedidoId).orElse(null);
	}

	@Override
	public List<Pedido> findAll() {
		// TODO Auto-generated method stub
		return pedidoRepository.findAll();
	}

	@Override
	public Pedido insertOne(Pedido pedido) {

		if(pedido.getId() == null || !pedidoRepository.existsById(pedido.getId())) {
	
			return pedidoRepository.save(pedido);
		}
		
		return null;
	}

	@Override
	public Pedido updateOne(Pedido pedido) {
		// TODO Auto-generated method stub
		if(pedidoRepository.existsById(pedido.getId())) {
			return pedidoRepository.save(pedido);
		}
		return null;
	}

	@Override
	public int deleteOne(ObjectId pedidoId) {
		// TODO Auto-generated method stub
		if(pedidoRepository.existsById(pedidoId)) {
			
			pedidoRepository.deleteById(pedidoId);
			return 1;
		}
		return 0;
	}
	
	

}
