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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
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
        if (eventLikeRepository.countLikes(event, userId) > 0)
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already like event with id " + eventId);
        if (eventDislikeRepository.countDislikes(event, userId) > 0)
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already dislike event with id " + eventId);

        eventLikeRepository.save(new EventLike(userId, event));
        eventRepository.addLike(eventId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void dislikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        if (eventLikeRepository.countLikes(event, userId) > 0)
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already like event with id " + eventId);
        if (eventDislikeRepository.countDislikes(event, userId) > 0)
            throw new ReactionAlreadyExistException("User with id " + userId
                    + " already dislike event with id " + eventId);

        eventDislikeRepository.save(new EventDislike(userId, event));
        eventRepository.addDislike(eventId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeLikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        EventLike eventLike = eventLikeRepository.findEventLikeByAuthorIdAndEvent(userId, event).orElseThrow(
                () -> new ReactionNotExistException("User with id " + userId + " didn't like event with id " + eventId)
        );
        eventLikeRepository.delete(eventLike);
        eventRepository.removeLike(eventId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeDislikeEvent(String userId, Long eventId) {
        Event event = getEventById(eventId);
        EventDislike eventDislike = eventDislikeRepository.findEventDislikeByAuthorIdAndEvent(userId, event)
                .orElseThrow(() -> new ReactionNotExistException("User with id " + userId
                        + " didn't dislike event with id " + eventId)
        );
        eventDislikeRepository.delete(eventDislike);
        eventRepository.removeLike(eventId);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
    }

}
