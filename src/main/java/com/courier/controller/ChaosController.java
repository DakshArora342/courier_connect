package com.courier.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/test/destination")
public class ChaosController {

    private final Random random = new Random();

    @PostMapping
    public ResponseEntity<String> unstableEndpoint(@RequestBody String body) {
        // Fail 70% of the time
        if (random.nextInt(10) < 7) {
            System.out.println("XXX Chaos Controller: Simulating FAILURE");
            return ResponseEntity.status(500).body("I am broken!");
        }
        System.out.println(">>> Chaos Controller: SUCCESS!");
        return ResponseEntity.ok("Received: " + body);
    }
}