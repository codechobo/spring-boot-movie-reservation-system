package com.example.springbootmoviereservationsystem.controller;

import com.example.springbootmoviereservationsystem.controller.dto.reservation.ReservationSaveRequestDto;
import com.example.springbootmoviereservationsystem.controller.dto.reservation.ReservationResponseDto;
import com.example.springbootmoviereservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponseDto> reserve(@RequestBody @Valid ReservationSaveRequestDto reservationSaveRequestDto) {
        ReservationResponseDto reservationSaveResponseDto = reservationService.reserveSave(reservationSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationSaveResponseDto);
    }

    @PostMapping("/reservation/{id}/ticket")
    public ResponseEntity<Void> createTicket(@PathVariable("id") Long reservationId) {
        reservationService.ticketPublish(reservationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/reservation/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable("id") Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
