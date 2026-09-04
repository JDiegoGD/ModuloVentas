package com.tecsup.ModuloVentas.Repository;

import com.tecsup.ModuloVentas.Model.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long> {

    List<Detalle> findByVenta_IdVenta(Long idVenta);

    boolean existsByProducto_IdProducto(Long idProducto);
}
