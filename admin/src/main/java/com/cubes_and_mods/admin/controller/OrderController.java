package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Order;
import com.cubes_and_mods.admin.jpa.repos.OrderRepos;
import com.cubes_and_mods.admin.security.CheckAdminService;
import com.cubes_and_mods.admin.service.ServiceOrder;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {

    @Autowired
    private OrderRepos orderRepository;

    @Autowired
    private ServiceOrder serviceOrder;

    @Autowired
    private CheckAdminService checkAdminService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(HttpServletRequest request) {
        checkAdminService.assertAllowed(request, admin -> admin.getCanOrders());
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/{code}/accept")
    public ResponseEntity<Void> acceptOrder(@PathVariable String code, HttpServletRequest request) {
        checkAdminService.assertAllowed(request, admin -> admin.getCanOrders());
        serviceOrder.confirmOrder(code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{code}/reject")
    public ResponseEntity<Void> rejectOrder(@PathVariable String code, HttpServletRequest request) {
        checkAdminService.assertAllowed(request, admin -> admin.getCanOrders());
        serviceOrder.rejectOrder(code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}