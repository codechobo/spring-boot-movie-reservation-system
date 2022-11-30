package com.example.springbootmoviereservationsystem.domain.consumer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Consumer> findByPhoneNumber(String phoneNumber);
}
