package com.faspix.repository;

import com.faspix.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("FROM Comment c " +
            "WHERE c.event.eventId = :eventId " +
            "ORDER BY c.creationDate")
    List<Comment> findCommentsByEventId(Long eventId);
}
