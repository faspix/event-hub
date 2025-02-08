package com.faspix.repository;

import com.faspix.entity.Request;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findRequestByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findRequestsByRequesterId(Long requesterId, Pageable pageable);

    List<Request> findRequestsByEventId(Long eventId, Pageable pageable);

}
