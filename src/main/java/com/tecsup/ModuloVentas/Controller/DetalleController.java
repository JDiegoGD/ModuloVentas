package com.tecsup.ModuloVentas.Controller;

import com.tecsup.ModuloVentas.Model.Detalle;
import com.tecsup.ModuloVentas.Service.DetalleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles")
public class DetalleController {

    private final DetalleService detalleService;

    public DetalleController(DetalleService detalleService) {
        this.detalleService = detalleService;
    }

    @GetMapping
    public ResponseEntity<List<Detalle>> findAll() {
        return ResponseEntity.ok(detalleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Detalle> findById(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Detalle> create(@RequestBody Detalle detalle) {
        Detalle creado = detalleService.create(detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Detalle> update(@PathVariable Long id, @RequestBody Detalle detalle) {
        return ResponseEntity.ok(detalleService.update(id, detalle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        detalleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
