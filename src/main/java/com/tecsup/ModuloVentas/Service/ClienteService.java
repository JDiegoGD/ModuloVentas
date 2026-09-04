package com.tecsup.ModuloVentas.Service;


import com.tecsup.ModuloVentas.Model.Cliente;
import com.tecsup.ModuloVentas.Repository.ClienteRepository;
import com.tecsup.ModuloVentas.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public ClienteService(ClienteRepository clienteRepository, VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + id));
    }

    public Cliente create(Cliente cliente) {
        validar(cliente);
        cliente.setIdCliente(null);
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente datos) {
        Cliente existente = findById(id);
        validar(datos);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setCargo(datos.getCargo());
        existente.setTelefono(datos.getTelefono());
        return clienteRepository.save(existente);
    }

    public void delete(Long id) {
        findById(id);
        if (ventaRepository.existsByCliente_IdCliente(id)) {
            throw new RuntimeException("No se puede eliminar el cliente, tiene ventas registradas");
        }
        clienteRepository.deleteById(id);
    }

    private void validar(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido del cliente es obligatorio");
        }
    }
}
