package com.courier.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.courier.entity.DeliveryTask;

public interface DeliveryRepository extends JpaRepository<DeliveryTask, UUID> {
}