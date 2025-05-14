package utility;

import com.faspix.shared.dto.RequestEndpointStatsDTO;
import com.faspix.entity.EndpointStats;
import com.faspix.shared.dto.ResponseEndpointStatsDTO;

import java.time.Instant;

public class StatisticsFactory {

    public static EndpointStats makeEndpointStats() {
        return EndpointStats.builder()
                .id(null)
                .ip("192.168.12.1")
                .app("event-service")
                .uri("/events/1")
                .timestamp(Instant.ofEpochSecond(1000000000))
                .build();
    }

    public static ResponseEndpointStatsDTO makeResponseEndpoint() {
        return ResponseEndpointStatsDTO.builder()
                .uri("/events/1")
                .app("event-service")
                .hits(1L)
                .build();
    }

    public static RequestEndpointStatsDTO makeRequestEndpoint() {
        return RequestEndpointStatsDTO.builder()
                .uri("/events/1")
                .app("event-service")
                .ip("192.168.12.1")
                .timestamp(Instant.ofEpochSecond(1000000000))
                .build();
    }

}
