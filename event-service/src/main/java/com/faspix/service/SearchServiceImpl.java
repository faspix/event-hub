package com.faspix.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.faspix.dto.*;
import com.faspix.entity.Event;
import com.faspix.entity.EventIndex;
import com.faspix.enums.EventState;
import com.faspix.exception.SearchServiceException;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.EventRepository;
import com.faspix.utility.EventSortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final EventViewService eventViewService;

    @Override
    public List<ResponseEventShortDTO> findEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  OffsetDateTime rangeStart,
                                                  OffsetDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  EventSortType sort,
                                                  Integer from,
                                                  Integer size
    ) {
        BoolQuery boolQuery = createSearchQuery(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        SearchResponse<EventIndex> searchResponse = safeElasticSearch(s -> s
                        .index("events")
                        .query(q -> q.bool(boolQuery))
                        .from(from)
                        .size(size)
                , EventIndex.class);

        List<Long> eventIds = searchResponse.hits().hits().stream()
                .map(hit -> Long.valueOf(hit.id()))
                .toList();

        List<Event> unorderedEvents = eventRepository.findAllById(eventIds);

        Map<Long, Event> eventMap = unorderedEvents.stream()
                .collect(Collectors.toMap(Event::getEventId, Function.identity()));

        List<Event> orderedEvents = eventIds.stream()
                .map(eventMap::get)
                .filter(Objects::nonNull)
                .toList();

        if (sort == EventSortType.VIEWS) {
            return orderedEvents
                    .stream()
                    .map(this::getResponseShortDTO)
                    .sorted((o1, o2) -> (int) (o1.getViews() - o2.getViews()))
                    .toList();
        } else {
            return orderedEvents
                    .stream()
                    .map(this::getResponseShortDTO)
                    .toList();
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<ResponseEventDTO> findEventsAdmin(List<String> users, List<EventState> states, List<Long> categories,
                                                  OffsetDateTime rangeStart, OffsetDateTime rangeEnd, Integer from,
                                                  Integer size) {
        Pageable pageRequest = makePageRequest(from, size);

        if (rangeStart == null)
            rangeStart = OffsetDateTime.now();
        if (rangeEnd == null)
            rangeEnd = OffsetDateTime.now().plusYears(1000);

        return eventRepository.searchEventAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest)
                .stream()
                .map(this::getResponseDTO)
                .toList();
    }


    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<ResponseEventShortDTO> findAllUsersEvents(String userId, Integer from, Integer size) {
        Pageable pageRequest = makePageRequest(from, size);
        return eventRepository.findEventsByInitiatorId(userId, pageRequest)
                .stream()
                .map(this::getResponseShortDTO)
                .toList();
    }

    private ResponseEventShortDTO getResponseShortDTO(Event event) {
        Long views = eventViewService.getViewsByEventId(event.getEventId());

        ResponseEventShortDTO responseDTO = eventMapper.eventToShortResponse(event);

        responseDTO.setViews(views);
        return responseDTO;
    }


    private ResponseEventDTO getResponseDTO(Event event) {
        Long views = eventViewService.getViewsByEventId(event.getEventId());

        ResponseEventDTO responseDTO = eventMapper.eventToResponse(event);
        responseDTO.setViews(views);
        return responseDTO;
    }

    private static BoolQuery createSearchQuery(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               OffsetDateTime rangeStart,
                                               OffsetDateTime rangeEnd,
                                               Boolean onlyAvailable
    ) {
        BoolQuery boolQuery = QueryBuilders.bool()
                .must(m ->m
                        .term(t -> t
                                .field("state")
                                .value("PUBLISHED")
                        )
                )
                .should(m -> {
                    if (!text.isBlank()) {
                        return m.match(q -> q
                                .field("title")
                                .query(text)
                                .fuzziness("AUTO")
                        );
                    } else {
                        return m.matchAll(ma -> ma);
                    }
                }).should(m -> {
                    if (!text.isBlank()) {
                        return m.match(q -> q
                                .field("annotation")
                                .query(text)
                                .fuzziness("AUTO")
                        );
                    } else {
                        return m.matchAll(ma -> ma);
                    }
                })
//                .minimumShouldMatch("0")
                .filter(f -> {
                    if (paid != null) {
                        return f.term(t -> t
                                .field("paid")
                                .value(paid)
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                }).filter(f -> {
                    if (rangeStart != null) {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .gte(rangeStart.toString())
                                )
                        );
                    } else {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .gte(OffsetDateTime.now().toString())
                                )
                        );
                    }
                }).filter(f -> {
                    if (rangeEnd != null) {
                        return f.range(r -> r
                                .date(d -> d
                                        .field("eventDate")
                                        .lte(rangeEnd.toString())
                                )
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                }).filter(f -> {
                    if (onlyAvailable) {
                        return f.bool(b -> b
                                .should(s -> s
                                        .term(t -> t
                                                .field("participantLimit").value(0)
                                        )
                                )
                                .should(s -> s.script(sc -> sc
                                        .script(scr -> scr
                                                .source("doc['participantLimit'].size() > 0 && doc['confirmedRequests'].size() > 0 && doc['confirmedRequests'].value < doc['participantLimit'].value")
                                                .lang("painless")
                                        )
                                ))
                                .minimumShouldMatch("1")
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                })
                .filter(f -> {
                    if (categories != null && !categories.isEmpty()) {
                        return f.terms(t -> t
                                .field("categoryId")
                                .terms(terms -> terms
                                        .value(categories.stream()
                                                .map(FieldValue::of)
                                                .toList()
                                        )
                                )
                        );
                    } else {
                        return f.matchAll(m -> m);
                    }
                })
                .build();
        return boolQuery;
    }

    private <TDocument> SearchResponse<TDocument> safeElasticSearch(Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<TDocument> tDocumentClass) {
        try {
            return elasticsearchClient.search((SearchRequest)((ObjectBuilder)fn.apply(new SearchRequest.Builder())).build(), tDocumentClass);
        } catch (IOException | ElasticsearchException e) {
            log.error("Elasticsearch search failed: {}", e.getMessage(), e);
            throw new SearchServiceException("Failed to search in Elasticsearch", e);
        }
    }

}
