package com.tysontoumi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Navn må ikke være tomt")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Kategori skal angives")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartCategory category;

    private String description;

    @NotNull(message = "Pris skal angives")
    @DecimalMin(value = "0.0", inclusive = false, message = "Pris skal være positiv")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Lagerantal skal angives")
    @Min(value = 0, message = "Lagerantal kan ikke være negativt")
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public Part() {}

    public Part(String name, PartCategory category, String description, Double price, Integer stock) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PartCategory getCategory() { return category; }
    public void setCategory(PartCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
