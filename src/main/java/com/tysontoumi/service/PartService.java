package com.tysontoumi.service;

import com.tysontoumi.exception.ResourceNotFoundException;
import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Part getPartById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Del med id " + id + " blev ikke fundet"));
    }

    public List<Part> getPartsByCategory(PartCategory category) {
        return partRepository.findByCategory(category);
    }

    public Map<PartCategory, List<Part>> getPartsGroupedByCategory() {
        return partRepository.findAll().stream()
                .filter(part -> part.getStock() > 0)
                .collect(Collectors.groupingBy(Part::getCategory));
    }

    public Part createPart(Part part) {
        return partRepository.save(part);
    }

    public Part updatePart(Long id, Part updatedPart) {
        Part existing = getPartById(id);
        existing.setName(updatedPart.getName());
        existing.setCategory(updatedPart.getCategory());
        existing.setDescription(updatedPart.getDescription());
        existing.setPrice(updatedPart.getPrice());
        existing.setStock(updatedPart.getStock());
        existing.setImageUrl(updatedPart.getImageUrl());
        return partRepository.save(existing);
    }

    public void deletePart(Long id) {
        Part part = getPartById(id);
        partRepository.delete(part);
    }

    public Part updateStock(Long id, int newStock) {
        if (newStock < 0) throw new IllegalArgumentException("Lagerantal kan ikke være negativt");
        Part part = getPartById(id);
        part.setStock(newStock);
        return partRepository.save(part);
    }
}
