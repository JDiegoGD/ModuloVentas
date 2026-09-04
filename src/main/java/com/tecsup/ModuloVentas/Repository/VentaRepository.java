package com.tecsup.ModuloVentas.Repository;

import com.tecsup.ModuloVentas.Model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    boolean existsByCliente_IdCliente(Long idCliente);

    boolean existsByEmpleado_IdEmpleado(Long idEmpleado);
}
