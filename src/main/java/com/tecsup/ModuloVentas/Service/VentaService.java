package com.tecsup.ModuloVentas.Service;

import com.tecsup.ModuloVentas.Model.Cliente;
import com.tecsup.ModuloVentas.Model.Detalle;
import com.tecsup.ModuloVentas.Model.Empleado;
import com.tecsup.ModuloVentas.Model.Producto;
import com.tecsup.ModuloVentas.Model.Venta;
import com.tecsup.ModuloVentas.Repository.ClienteRepository;
import com.tecsup.ModuloVentas.Repository.DetalleRepository;
import com.tecsup.ModuloVentas.Repository.EmpleadoRepository;
import com.tecsup.ModuloVentas.Repository.ProductoRepository;
import com.tecsup.ModuloVentas.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DetalleRepository detalleRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ClienteRepository clienteRepository,
                         EmpleadoRepository empleadoRepository, DetalleRepository detalleRepository,
                         ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id " + id));
    }

    @Transactional(readOnly = true)
    public List<Detalle> findDetalles(Long idVenta) {
        findById(idVenta);
        return detalleRepository.findByVenta_IdVenta(idVenta);
    }

    public Venta create(Venta venta) {
        Cliente cliente = resolverCliente(venta);
        Empleado empleado = resolverEmpleado(venta);
        venta.setIdVenta(null);
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setFecha(venta.getFecha() != null ? venta.getFecha() : LocalDate.now());
        venta.setTotal(0.0);
        return ventaRepository.save(venta);
    }

    public Venta update(Long id, Venta datos) {
        Venta existente = findById(id);
        Cliente cliente = resolverCliente(datos);
        Empleado empleado = resolverEmpleado(datos);
        existente.setFecha(datos.getFecha() != null ? datos.getFecha() : existente.getFecha());
        existente.setCliente(cliente);
        existente.setEmpleado(empleado);
        Venta guardada = ventaRepository.save(existente);
        recalcularTotal(guardada.getIdVenta());
        return findById(id);
    }

    public void delete(Long id) {
        findById(id);
        List<Detalle> detalles = detalleRepository.findByVenta_IdVenta(id);
        for (Detalle detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        detalleRepository.deleteAll(detalles);
        ventaRepository.deleteById(id);
    }

    void recalcularTotal(Long idVenta) {
        List<Detalle> detalles = detalleRepository.findByVenta_IdVenta(idVenta);
        double total = detalles.stream().mapToDouble(Detalle::getSubtotal).sum();
        Venta venta = findById(idVenta);
        venta.setTotal(total);
        ventaRepository.save(venta);
    }

    private Cliente resolverCliente(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null) {
            throw new IllegalArgumentException("Debe indicar el cliente (id_cliente) de la venta");
        }
        Long idCliente = venta.getCliente().getIdCliente();
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + idCliente));
    }

    private Empleado resolverEmpleado(Venta venta) {
        if (venta.getEmpleado() == null || venta.getEmpleado().getIdEmpleado() == null) {
            throw new IllegalArgumentException("Debe indicar el empleado (id_empleado) de la venta");
        }
        Long idEmpleado = venta.getEmpleado().getIdEmpleado();
        return empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id " + idEmpleado));
    }
}
