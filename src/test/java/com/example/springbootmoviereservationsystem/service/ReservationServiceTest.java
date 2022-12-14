package com.example.springbootmoviereservationsystem.service;

import com.example.springbootmoviereservationsystem.controller.reservation.dto.PopularMovieResponseDto;
import com.example.springbootmoviereservationsystem.controller.reservation.dto.ReservationResponseDto;
import com.example.springbootmoviereservationsystem.domain.consumer.Consumer;
import com.example.springbootmoviereservationsystem.util.Money;
import com.example.springbootmoviereservationsystem.domain.movie.Movie;
import com.example.springbootmoviereservationsystem.domain.reservation.Reservation;
import com.example.springbootmoviereservationsystem.domain.reservation.ReservationRepository;
import com.example.springbootmoviereservationsystem.domain.reservation.ReservationStatus;
import com.example.springbootmoviereservationsystem.domain.screening.Screening;
import com.example.springbootmoviereservationsystem.domain.seat.Seat;
import com.example.springbootmoviereservationsystem.domain.ticket.Ticket;
import com.example.springbootmoviereservationsystem.domain.ticket.TicketRepository;
import com.example.springbootmoviereservationsystem.fixture.CreateDto;
import com.example.springbootmoviereservationsystem.fixture.CreateEntity;
import com.example.springbootmoviereservationsystem.infra.policy.DiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private SeatService seatService;

    @Mock
    private ConsumerService consumerService;

    @Mock
    private ScreeningService screeningService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private DiscountPolicy discountPolicy;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????????")
    void reserveSave() {
        // given
        Consumer consumer = CreateEntity.createConsumer();
        Movie movie = CreateEntity.createMovie();
        Screening screening = CreateEntity.createScreening(movie);
        Seat seat = CreateEntity.createSingleSeat(1);
        given(discountPolicy.calculateDiscountAmount(any())).willReturn(Money.ZERO);
        Reservation reservation = screening.reserve(consumer, 1, Money.ZERO);
        seat.reserve(reservation);

        given(consumerService.findConsumer(any())).willReturn(consumer);
        given(screeningService.findScreen(any())).willReturn(screening);
        given(seatService.findSeat(any())).willReturn(seat);
        given(reservationRepository.save(any())).willReturn(reservation);

        // when
        ReservationResponseDto result = reservationService.reserveSave(CreateDto.CreateReservationSaveRequestDto());

        // then
        assertThat(result.getAudienceCount()).isEqualTo(reservation.getAudienceCount());
        assertThat(result.getConsumerResponseDto().getNickname()).isEqualTo(consumer.getNickname());
        assertThat(result.getScreeningSaveResponseDto().getMovie().getTitle()).isEqualTo(movie.getTitle());
        assertThat(result.getSeats().size()).isEqualTo(reservation.getAudienceCount());

        verify(consumerService, atLeastOnce()).findConsumer(any());
        verify(screeningService, atLeastOnce()).findScreen(any());
        verify(seatService, times(reservation.getAudienceCount())).findSeat(any());
        verify(reservationRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????? ???????????? ?????????")
    void ticketPublish() {
        // given
        Consumer consumer = CreateEntity.createConsumer();
        Movie movie = CreateEntity.createMovie();
        Screening screening = CreateEntity.createScreening(movie);
        Reservation reservation = screening.reserve(consumer, 5, Money.ZERO);
        Ticket ticket = reservation.publishTicket();

        given(reservationRepository.findById(any())).willReturn(Optional.of(reservation));
        given(ticketRepository.save(any())).willReturn(ticket);

        // when
        reservationService.ticketPublish(1L);

        // then
        assertThat(ticket).isEqualTo(consumer.getTicket());
        assertThat(ticket.isPublish()).isTrue();

        verify(reservationRepository, atLeastOnce()).findById(any());
        verify(ticketRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("?????? ???????????? ?????????")
    void cancelReservation() {
        // given
        Consumer consumer = CreateEntity.createConsumer();
        Movie movie = CreateEntity.createMovie();
        Screening screening = CreateEntity.createScreening(movie);
        Reservation reservation = screening.reserve(consumer, 5, Money.ZERO);

        given(reservationRepository.findById(any())).willReturn(Optional.of(reservation));

        // when
        reservationService.cancelReservation(1L);

        // then
        assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.CANCELLED_BY_CUSTOMER);

        verify(reservationRepository).findById(any());
    }

    @Test
    @DisplayName("?????? ???????????? ?????? ????????? (????????? ?????? ?????? ??? ??????")
    void cancelReservation_Exception() {
        // given
        Consumer consumer = CreateEntity.createConsumer();
        Movie movie = CreateEntity.createMovie();
        Screening screening = CreateEntity.createScreening(movie);
        Reservation reservation = screening.reserve(consumer, 5, Money.ZERO);
        Ticket ticket = reservation.publishTicket();

        given(ticketRepository.save(any())).willReturn(ticket);
        given(reservationRepository.findById(any())).willReturn(Optional.of(reservation));

        reservationService.ticketPublish(1L);

        // when && then
        assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("?????? ?????? ?????? ????????? ????????????!");

        verify(ticketRepository).save(any());
        verify(reservationRepository, atMost(2)).findById(any());
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ?????? ????????????")
    void ReservationServiceTest() {
        // given
        Consumer consumer = CreateEntity.createConsumer();
        Movie movie = CreateEntity.createMovie();
        Screening screening = CreateEntity.createScreening(movie);
        Reservation reserve = screening.reserve(consumer, 5, Money.ZERO);

        Long count = 10L;
        given(reservationRepository.findBestMovie(any())).willReturn(List.of(PopularMovieResponseDto.builder()
                .movie(reserve.getScreening().getMovie())
                .reserveCount(count)
                .build()));

        // when
        List<PopularMovieResponseDto> result = reservationService.bestMovieFind();

        // then
        assertThat(screening.getMovie().getTitle()).isEqualTo(result.get(0).getMovie().getTitle());
        assertThat(count).isEqualTo(result.get(0).getReserveCount());

        verify(reservationRepository).findBestMovie(any());
    }

}