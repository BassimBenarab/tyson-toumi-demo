package com.tysontoumi.service;

import com.tysontoumi.dto.OrderRequest;
import com.tysontoumi.exception.ResourceNotFoundException;
import com.tysontoumi.model.*;
import com.tysontoumi.repository.CustomerRepository;
import com.tysontoumi.repository.PartRepository;
import com.tysontoumi.repository.WatchOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final WatchOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PartRepository partRepository;

    public OrderService(WatchOrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        PartRepository partRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.partRepository = partRepository;
    }

    public List<WatchOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public WatchOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordre med id " + id + " blev ikke fundet"));
    }

    public List<WatchOrder> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public WatchOrder createOrder(OrderRequest request) {
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer(
                            request.getCustomerName(),
                            request.getCustomerEmail(),
                            request.getCustomerPhone()
                    );
                    return customerRepository.save(newCustomer);
                });

        double totalPrice = request.getItems().stream()
                .mapToDouble(item -> {
                    Part part = partRepository.findById(item.partId())
                            .orElseThrow(() -> new ResourceNotFoundException("Del med id " + item.partId() + " findes ikke"));
                    if (part.getStock() < item.quantity()) {
                        throw new IllegalArgumentException("Ikke nok på lager for: " + part.getName());
                    }
                    return part.getPrice() * item.quantity();
                })
                .sum();

        WatchOrder order = new WatchOrder(customer, totalPrice, request.getNotes());
        WatchOrder savedOrder = orderRepository.save(order);

        request.getItems().forEach(itemReq -> {
            Part part = partRepository.findById(itemReq.partId()).get();
            part.setStock(part.getStock() - itemReq.quantity());
            partRepository.save(part);

            OrderItem item = new OrderItem(savedOrder, part, itemReq.quantity());
            savedOrder.getItems().add(item);
        });

        return orderRepository.save(savedOrder);
    }

    @Transactional
    public WatchOrder updateStatus(Long id, OrderStatus newStatus) {
        WatchOrder order = getOrderById(id);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<WatchOrder> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
