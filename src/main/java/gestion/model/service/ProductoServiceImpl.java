package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gestion.model.collections.Producto;
import gestion.model.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {
	
	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public Producto findById(ObjectId idProducto) {
		// TODO Auto-generated method stub
		return productoRepository.findById(idProducto).orElse(null);
	}

	@Override
	public List<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

	@Override
	public Producto insertOne(Producto producto) {
		
		if(producto.getId() == null || !productoRepository.existsById(producto.getId())) {
			Producto aux = producto;
			
			aux.calcularIva();
			return productoRepository.save(aux);
		}
			return null;
	}

	@Override
	public Producto updateOne(Producto producto) {
		
		if(productoRepository.existsById(producto.getId())) {
			Producto aux = producto;
			aux.calcularIva();
			return productoRepository.save(aux);
		}
		return null;
	}

	@Override
	public int deleteOne(ObjectId idProducto) {
		if(productoRepository.existsById(idProducto)) {
			
			productoRepository.deleteById(idProducto);
			return 1;
		}
		return 0;
	}

}
