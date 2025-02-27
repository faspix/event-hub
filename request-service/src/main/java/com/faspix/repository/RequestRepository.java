package com.faspix.repository;

import com.faspix.entity.Request;
import com.faspix.enums.ParticipationRequestState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findRequestByRequesterIdAndEventId(String userId, Long eventId);

    List<Request> findRequestsByRequesterId(String requesterId, Pageable pageable);

    List<Request> findRequestsByEventId(Long eventId, Pageable pageable);

    List<Request> findRequestsByEventIdAndState(Long eventId, ParticipationRequestState state);

    @Query("SELECT COUNT(r) FROM Request r " +
            "WHERE r.eventId = :eventId " +
            "AND r.state = 'CONFIRMED' ")
    Integer getAcceptedEventsCount(Long eventId);
}
