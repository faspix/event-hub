package com.faspix.service;

import com.faspix.entity.Event;
import com.faspix.entity.EventDislike;
import com.faspix.entity.EventLike;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ReactionAlreadyExistException;
import com.faspix.exception.ReactionNotExistException;
import com.faspix.repository.EventDislikeRepository;
import com.faspix.repository.EventLikeRepository;
import com.faspix.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventReactionServiceImpl implements EventReactionService {

    private final EventLikeRepository eventLikeRepository;

    private final EventDislikeRepository eventDislikeRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void likeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        validateReaction(userId, eventId, event);

        eventLikeRepository.save(new EventLike(userId, event));
        eventRepository.addLike(eventId);
        log.info("User {} liked event {}", userId, eventId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void dislikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        validateReaction(userId, eventId, event);

        eventDislikeRepository.save(new EventDislike(userId, event));
        eventRepository.addDislike(eventId);
        log.info("User {} disliked event {}", userId, eventId);
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeLikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        EventLike eventLike = eventLikeRepository.findByAuthorIdAndEvent(userId, event).orElseThrow(
                () -> new ReactionNotExistException("User with id " + userId + " didn't like event with id " + eventId)
        );
        eventLikeRepository.delete(eventLike);
        eventRepository.removeLike(eventId);
        log.info("User {} remove like from event {}", userId, eventId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeDislikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        EventDislike eventDislike = eventDislikeRepository.findByAuthorIdAndEvent(userId, event)
                .orElseThrow(() -> new ReactionNotExistException("User with id " + userId
                        + " didn't dislike event with id " + eventId)
        );
        eventDislikeRepository.delete(eventDislike);
        eventRepository.removeDislike(eventId);
        log.info("User {} remove dislike from event {}", userId, eventId);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
    }

    private void validateReaction(String userId, Long eventId, Event event) {
        if (eventLikeRepository.existsByEventAndAuthorId(event, userId))
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already liked event with id " + eventId);
        if (eventDislikeRepository.existsByEventAndAuthorId(event, userId))
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already disliked event with id " + eventId);
    }
}
