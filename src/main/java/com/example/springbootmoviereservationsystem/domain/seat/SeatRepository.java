package com.example.springbootmoviereservationsystem.domain.seat;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @EntityGraph(value = "seatWithReservation")
    Optional<Seat> findById(Long seatId);
}
