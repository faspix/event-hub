package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findEventsByInitiatorId(String userId, Pageable pageable);

    List<Event> findEventsByCategoryId(Long categoryId);

    @Modifying
    @Query("UPDATE Event e " +
            "SET e.likes = e.likes + 1 " +
            "WHERE e.eventId = :eventId")
    void addLike(Long eventId);

    @Modifying
    @Query("UPDATE Event e " +
            "SET e.likes = e.likes - 1 " +
            "WHERE e.eventId = :eventId")
    void removeLike(Long eventId);

    @Modifying
    @Query("UPDATE Event e " +
            "SET e.dislikes = e.dislikes + 1 " +
            "WHERE e.eventId = :eventId")
    void addDislike(Long eventId);

    @Modifying
    @Query("UPDATE Event e " +
            "SET e.dislikes = e.dislikes - 1 " +
            "WHERE e.eventId = :eventId")
    void removeDislike(Long eventId);

    @Query("SELECT e FROM Event e WHERE " +
            "e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.description) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.categoryId IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)")
    Page<Event> searchEvent(@Param("text") String text,
                            @Param("categories") List<Long> categories,
                            @Param("paid") Boolean paid,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            @Param("onlyAvailable") Boolean onlyAvailable,
                            Pageable pageable);


    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiatorId IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.categoryId IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd) ")
    Page<Event> searchEventAdmin(@Param("users") List<String> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);


}