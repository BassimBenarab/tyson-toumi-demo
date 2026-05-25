package com.tysontoumi.repository;

import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByCategory(PartCategory category);
    List<Part> findByStockGreaterThan(int minStock);
}
