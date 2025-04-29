package com.faspix.utility;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class SafeElasticSearch {

    private final ElasticsearchClient elasticsearchClient;

    public <TDocument> SearchResponse<TDocument> search(Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<TDocument> tDocumentClass) {
        try {
            return elasticsearchClient.search((SearchRequest)((ObjectBuilder)fn.apply(new SearchRequest.Builder())).build(), tDocumentClass);
        } catch (IOException | ElasticsearchException e) {
            throw new RuntimeException("Failed to search in Elasticsearch", e);
        }
    }

}
