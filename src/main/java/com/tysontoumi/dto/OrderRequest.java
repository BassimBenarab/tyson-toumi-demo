package com.tysontoumi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequest {

    @NotBlank(message = "Kundenavn må ikke være tomt")
    private String customerName;

    @NotBlank(message = "Email må ikke være tom")
    @Email(message = "Ugyldig emailadresse")
    private String customerEmail;

    private String customerPhone;

    @NotEmpty(message = "Ordren skal indeholde mindst ét emne")
    private List<OrderItemRequest> items;

    private String notes;

    // Nested record for hvert valgt del
    public record OrderItemRequest(Long partId, int quantity) {}

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
