package com.tysontoumi.controller;

import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.service.PartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = "*")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public ResponseEntity<List<Part>> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<PartCategory, List<Part>>> getPartsGrouped() {
        return ResponseEntity.ok(partService.getPartsGroupedByCategory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Part>> getByCategory(@PathVariable PartCategory category) {
        return ResponseEntity.ok(partService.getPartsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Part> createPart(@Valid @RequestBody Part part) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partService.createPart(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @Valid @RequestBody Part part) {
        return ResponseEntity.ok(partService.updatePart(id, part));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Part> updateStock(@PathVariable Long id, @RequestParam int stock) {
        return ResponseEntity.ok(partService.updateStock(id, stock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
