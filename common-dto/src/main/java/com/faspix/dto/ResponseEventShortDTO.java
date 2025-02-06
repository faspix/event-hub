package com.faspix.dto;


import java.time.LocalDateTime;

public class ResponseEventShortDTO {

    private Long eventId;

    private String title;

    private String annotation;

    // category

    private Integer confirmedRequests;

    private LocalDateTime eventDate;

    private ResponseUserShortDTO initiator;

    private Boolean paid;

    private Integer views;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Integer getConfirmedRequests() {
        return confirmedRequests;
    }

    public void setConfirmedRequests(Integer confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public ResponseUserShortDTO getInitiator() {
        return initiator;
    }

    public void setInitiator(ResponseUserShortDTO initiator) {
        this.initiator = initiator;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }
}
