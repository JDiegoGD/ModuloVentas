package com.tecsup.ModuloVentas.Service;

import com.tecsup.ModuloVentas.Model.Categoria;
import com.tecsup.ModuloVentas.Model.Producto;
import com.tecsup.ModuloVentas.Repository.CategoriaRepository;
import com.tecsup.ModuloVentas.Repository.DetalleRepository;
import com.tecsup.ModuloVentas.Repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetalleRepository detalleRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                            DetalleRepository detalleRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detalleRepository = detalleRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> findByCategoria(Long idCategoria) {
        categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id " + idCategoria));
        return productoRepository.findByCategoria_IdCategoria(idCategoria);
    }

    public Producto create(Producto producto) {
        validarDatos(producto);
        Categoria categoria = resolverCategoria(producto);
        producto.setIdProducto(null);
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto datos) {
        Producto existente = findById(id);
        validarDatos(datos);
        Categoria categoria = resolverCategoria(datos);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setCategoria(categoria);
        return productoRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        if (detalleRepository.existsByProducto_IdProducto(id)) {
            throw new RuntimeException("No se puede eliminar el producto, tiene detalles de venta asociados");
        }
        productoRepository.deleteById(id);
    }

    private Categoria resolverCategoria(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("Debe indicar la categoria (id_categoria) del producto");
        }
        Long idCategoria = producto.getCategoria().getIdCategoria();
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id " + idCategoria));
    }

    private void validarDatos(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio del producto debe ser un valor valido");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock del producto debe ser un valor valido");
        }
    }
}
