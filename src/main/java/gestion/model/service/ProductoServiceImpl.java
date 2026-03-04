package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import gestion.model.collections.Producto;
import gestion.model.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public Producto findById(ObjectId idProducto) {
        return productoRepository.findById(idProducto).orElse(null);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> findDisponiblesByRestaurante(ObjectId restauranteId) {
        return productoRepository.findByRestauranteIdAndDisponibleTrue(restauranteId);
    }

    @Override
    public Producto insertOne(Producto producto) {
        if (producto.getId() == null || !productoRepository.existsById(producto.getId())) {
            producto.calcularIva();
            return productoRepository.save(producto);
        }
        return null;
    }

    @Override
    public Producto updateOne(Producto producto) {
        if (productoRepository.existsById(producto.getId())) {
            producto.calcularIva();
            return productoRepository.save(producto);
        }
        return null;
    }

    @Override
    public int deleteOne(ObjectId idProducto) {
        if (productoRepository.existsById(idProducto)) {
            productoRepository.deleteById(idProducto);
            return 1;
        }
        return 0;
    }
}
