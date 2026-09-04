package com.tecsup.ModuloVentas.Repository;

import com.tecsup.ModuloVentas.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria_IdCategoria(Long idCategoria);

    boolean existsByCategoria_IdCategoria(Long idCategoria);
}