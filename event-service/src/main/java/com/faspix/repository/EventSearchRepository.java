package com.faspix.repository;

import com.faspix.domain.index.EventIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventIndex, Long> {




}
