package com.tecsup.ModuloVentas.Controller;

import com.tecsup.ModuloVentas.Model.Detalle;
import com.tecsup.ModuloVentas.Model.Venta;
import com.tecsup.ModuloVentas.Service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<List<Venta>> findAll() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findById(id));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<Detalle>> findDetalles(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.findDetalles(id));
    }

    @PostMapping
    public ResponseEntity<Venta> create(@RequestBody Venta venta) {
        Venta creada = ventaService.create(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> update(@PathVariable Long id, @RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.update(id, venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
