package com.example.springbootmoviereservationsystem.domain.screening;

import com.example.springbootmoviereservationsystem.controller.screening.dto.ScreenDtoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    Page<ScreenDtoProjection> findByMovieTitleStartingWithAndWhenScreenedGreaterThanEqual(String title,
                                                                                          LocalDateTime whenScreened,
                                                                                          Pageable pageable);

    @EntityGraph(value = "screeningWithMovie", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Screening> findById(Long screenId);
}