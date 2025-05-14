package com.faspix.repository;

import com.faspix.domain.entity.Event;
import com.faspix.shared.utility.EventState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findEventsByInitiatorId(String userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.eventId IN :ids")
    Page<Event> findAllEventsByIds(@Param("ids") Set<Long> ids, Pageable pageable);

    boolean existsEventsByCategoryId(Long categoryId);

    @Modifying
    @Query("""
    UPDATE Event e
    SET e.initiatorUsername = :name
    WHERE e.initiatorId = :userId
    """)
    void updateEventInitiatorName(String userId, String name);

    @Modifying
    @Query("""
    UPDATE Event e
    SET e.categoryName = :catName
    WHERE e.categoryId = :catId
    """)
    void updateCategoryName(Long catId, String catName);

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
            "(:users IS NULL OR e.initiatorId IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.categoryId IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd) ")
    Page<Event> searchEventAdmin(@Param("users") List<String> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") OffsetDateTime rangeStart,
                                 @Param("rangeEnd") OffsetDateTime rangeEnd,
                                 Pageable pageable);

}
