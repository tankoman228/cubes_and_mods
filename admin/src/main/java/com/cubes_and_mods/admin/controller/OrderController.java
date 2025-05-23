package com.cubes_and_mods.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes_and_mods.admin.jpa.Order;

@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {

    // TODO: реализовать работу с заказами
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
        @RequestParam(defaultValue = "false") boolean showClosed
    ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{code}/accept")
    public ResponseEntity<Void> acceptOrder(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{code}/reject")
    public ResponseEntity<Void> rejectOrder(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}