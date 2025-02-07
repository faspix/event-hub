package com.faspix.mapper;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.ResponseEventShortDTO;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapperUtil {

    private final EventServiceClient eventServiceClient;

    private final EventMapper eventMapper;

    @Named("getEventsDto")
    List<ResponseEventShortDTO> getEventsDto(List<Long> events) {
        return events.stream()
                .map(
                        (eventId) -> eventMapper.eventToShortEvent(
                                eventServiceClient.getEventById(eventId)
                        )
                ).toList();
    }

}
