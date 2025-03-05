package com.faspix.service;

public interface EventReactionService {

    void likeEvent(String userId, Long eventId);

    void dislikeEvent(String userId, Long eventId);

    void removeLikeEvent(String userId, Long eventId);

    void removeDislikeEvent(String userId, Long eventId);

}
