package com.tecsup.ModuloVentas.Service;

import com.tecsup.ModuloVentas.Model.Categoria;
import com.tecsup.ModuloVentas.Repository.CategoriaRepository;
import com.tecsup.ModuloVentas.Repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id " + id));
    }

    public Categoria create(Categoria categoria) {
        validar(categoria);
        categoria.setIdCategoria(null);
        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria datos) {
        Categoria existente = findById(id);
        validar(datos);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        return categoriaRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        if (productoRepository.existsByCategoria_IdCategoria(id)) {
            throw new RuntimeException("No se puede eliminar la categoria, tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }

    private void validar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoria es obligatorio");
        }
    }
}
