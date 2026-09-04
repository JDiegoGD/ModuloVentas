package com.tecsup.ModuloVentas.Service;

import com.tecsup.ModuloVentas.Model.Detalle;
import com.tecsup.ModuloVentas.Model.Producto;
import com.tecsup.ModuloVentas.Model.Venta;
import com.tecsup.ModuloVentas.Repository.DetalleRepository;
import com.tecsup.ModuloVentas.Repository.ProductoRepository;
import com.tecsup.ModuloVentas.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DetalleService {

    private final DetalleRepository detalleRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final VentaService ventaService;

    public DetalleService(DetalleRepository detalleRepository, VentaRepository ventaRepository,
                           ProductoRepository productoRepository, VentaService ventaService) {
        this.detalleRepository = detalleRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.ventaService = ventaService;
    }

    @Transactional(readOnly = true)
    public List<Detalle> findAll() {
        return detalleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Detalle findById(Long id) {
        return detalleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con id " + id));
    }

    public Detalle create(Detalle input) {
        Venta venta = resolverVenta(input);
        Producto producto = resolverProducto(input);
        Integer cantidad = input.getCantidad();
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto '" + producto.getNombre()
                    + "' (disponible: " + producto.getStock() + ")");
        }
        Double precio = input.getPrecio() != null ? input.getPrecio() : producto.getPrecio();

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        Detalle detalle = new Detalle();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setSubtotal(precio * cantidad);
        Detalle guardado = detalleRepository.save(detalle);

        ventaService.recalcularTotal(venta.getIdVenta());
        return guardado;
    }

    public Detalle update(Long id, Detalle input) {
        Detalle existente = findById(id);
        Producto productoActual = existente.getProducto();
        Integer cantidadActual = existente.getCantidad();

        Producto nuevoProducto = (input.getProducto() != null && input.getProducto().getIdProducto() != null)
                ? productoRepository.findById(input.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado con id " + input.getProducto().getIdProducto()))
                : productoActual;

        Integer nuevaCantidad = input.getCantidad() != null ? input.getCantidad() : cantidadActual;
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        Double nuevoPrecio = input.getPrecio() != null ? input.getPrecio() : existente.getPrecio();

        if (productoActual.getIdProducto().equals(nuevoProducto.getIdProducto())) {
            int stockDisponible = productoActual.getStock() + cantidadActual - nuevaCantidad;
            if (stockDisponible < 0) {
                throw new RuntimeException("Stock insuficiente para el producto '" + productoActual.getNombre() + "'");
            }
            productoActual.setStock(stockDisponible);
            productoRepository.save(productoActual);
        } else {
            productoActual.setStock(productoActual.getStock() + cantidadActual);
            productoRepository.save(productoActual);

            if (nuevoProducto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para el producto '" + nuevoProducto.getNombre() + "'");
            }
            nuevoProducto.setStock(nuevoProducto.getStock() - nuevaCantidad);
            productoRepository.save(nuevoProducto);
        }

        existente.setProducto(nuevoProducto);
        existente.setCantidad(nuevaCantidad);
        existente.setPrecio(nuevoPrecio);
        existente.setSubtotal(nuevoPrecio * nuevaCantidad);
        Detalle guardado = detalleRepository.save(existente);

        ventaService.recalcularTotal(existente.getVenta().getIdVenta());
        return guardado;
    }

    public void delete(Long id) {
        Detalle existente = findById(id);
        Producto producto = existente.getProducto();
        producto.setStock(producto.getStock() + existente.getCantidad());
        productoRepository.save(producto);

        Long idVenta = existente.getVenta().getIdVenta();
        detalleRepository.delete(existente);
        ventaService.recalcularTotal(idVenta);
    }

    private Venta resolverVenta(Detalle detalle) {
        if (detalle.getVenta() == null || detalle.getVenta().getIdVenta() == null) {
            throw new IllegalArgumentException("Debe indicar la venta (id_venta) del detalle");
        }
        Long idVenta = detalle.getVenta().getIdVenta();
        return ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id " + idVenta));
    }

    private Producto resolverProducto(Detalle detalle) {
        if (detalle.getProducto() == null || detalle.getProducto().getIdProducto() == null) {
            throw new IllegalArgumentException("Debe indicar el producto (id_producto) del detalle");
        }
        Long idProducto = detalle.getProducto().getIdProducto();
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id " + idProducto));
    }
}
