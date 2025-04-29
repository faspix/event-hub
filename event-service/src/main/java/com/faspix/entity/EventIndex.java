package com.faspix.entity;

import com.faspix.enums.EventState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.OffsetDateTime;

@Document(indexName = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventIndex {

    @Id
    private Long eventId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String annotation;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime eventDate;

    @Field(type = FieldType.Boolean)
    private Boolean paid;

    @Field(type = FieldType.Keyword)
    private EventState state;

    @Field(type = FieldType.Integer)
    private Integer participantLimit;

    @Field(type = FieldType.Integer)
    private Integer confirmedRequests;

}