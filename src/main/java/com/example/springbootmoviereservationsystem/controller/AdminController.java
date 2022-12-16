package com.example.springbootmoviereservationsystem.controller;

import com.example.springbootmoviereservationsystem.controller.dto.consumer.ConsumerSaveAndUpdateRequestDto;
import com.example.springbootmoviereservationsystem.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.springbootmoviereservationsystem.controller.dto.consumer.ConsumerResponseDto.ConsumerDetailResponseDto;

@Valid
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ConsumerService consumerService;

    @GetMapping("/admin/consumers/{id}")
    public ResponseEntity<ConsumerDetailResponseDto> consumerDetail(@PathVariable("id") Long consumerId) {
        ConsumerDetailResponseDto consumerDetailResponseDto =
                ConsumerDetailResponseDto.of(consumerService.findConsumer(consumerId));
        return ResponseEntity.status(HttpStatus.OK).body(consumerDetailResponseDto);
    }

    @PutMapping("/admin/consumer/{id}")
    public ResponseEntity<Void> consumerUpdate(@PathVariable("id") Long consumerId,
                                               @RequestBody ConsumerSaveAndUpdateRequestDto consumerSaveAndUpdateRequestDto) {
        consumerService.updateConsumer(consumerId, consumerSaveAndUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
