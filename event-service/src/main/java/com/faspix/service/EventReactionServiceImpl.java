package com.faspix.service;

import com.faspix.entity.Event;
import com.faspix.entity.EventLikes;
import com.faspix.exception.EventNotFoundException;
import com.faspix.exception.ReactionAlreadyExistException;
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

    private final EventRepository eventRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void likeEvent(String userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event with id " + eventId + " not found")
        );
        if (eventLikeRepository.countLikes(event, userId) > 0)
            throw new ReactionAlreadyExistException("User with id " + userId + " already like event with id " + eventId);
        eventLikeRepository.save(new EventLikes(userId, event));
        eventRepository.updateLikes(eventId);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void dislikeEvent(String userId, Long eventId) {

    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeLikeEvent(String userId, Long eventId) {

    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void removeDislikeEvent(String userId, Long eventId) {

    }
}
