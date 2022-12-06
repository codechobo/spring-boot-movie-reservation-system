package com.example.springbootmoviereservationsystem.domain.repository;

import com.example.springbootmoviereservationsystem.domain.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Consumer> findByPhoneNumber(String phoneNumber);
}