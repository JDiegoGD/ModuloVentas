package com.tecsup.ModuloVentas.Service;

import com.tecsup.ModuloVentas.Model.Empleado;
import com.tecsup.ModuloVentas.Repository.EmpleadoRepository;
import com.tecsup.ModuloVentas.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final VentaRepository ventaRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, VentaRepository ventaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Empleado findById(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con id " + id));
    }

    public Empleado create(Empleado empleado) {
        validar(empleado);
        empleado.setIdEmpleado(null);
        return empleadoRepository.save(empleado);
    }

    public Empleado update(Long id, Empleado datos) {
        Empleado existente = findById(id);
        validar(datos);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCargo(datos.getCargo());
        existente.setTelefono(datos.getTelefono());
        return empleadoRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        if (ventaRepository.existsByEmpleado_IdEmpleado(id)) {
            throw new RuntimeException("No se puede eliminar el empleado, tiene ventas registradas");
        }
        empleadoRepository.deleteById(id);
    }

    private void validar(Empleado empleado) {
        if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del empleado es obligatorio");
        }
        if (empleado.getApellido() == null || empleado.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido del empleado es obligatorio");
        }
    }
}
