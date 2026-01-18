package gestion.model.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gestion.model.collections.Categoria;
import gestion.model.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@Override
	public Categoria findById(ObjectId categoriaId) {
		// TODO Auto-generated method stub
		return categoriaRepository.findById(categoriaId).orElse(null);
	}

	@Override
	public List<Categoria> findAll() {
		// TODO Auto-generated method stub
		return categoriaRepository.findAll();
	}

	@Override
	public Categoria insertOne(Categoria categoria) {

		if(categoria.getId() == null || !categoriaRepository.existsById(categoria.getId())) {
			
			return categoriaRepository.save(categoria);
		}
		return null;
	}

	@Override
	public Categoria updateOne(Categoria categoria) {
		
		if(categoriaRepository.existsById(categoria.getId())) {
			return categoriaRepository.save(categoria);
		}
		return null;
	}

	@Override
	public int deleteOne(ObjectId categoriaId) {
		if(categoriaRepository.existsById(categoriaId)) {
			categoriaRepository.deleteById(categoriaId);
			return 1;
		}
		return 0;
	}

}
