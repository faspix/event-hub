package service;

import com.faspix.dto.ResponseEndpointStatsDTO;
import com.faspix.mapper.StatisticsMapper;
import com.faspix.dao.StatisticsRepository;
import com.faspix.service.StatisticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static utility.StatisticsFactory.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Spy
    private StatisticsMapper statisticsMapper = Mappers.getMapper(StatisticsMapper.class);

    @Test
    public void getEndpointStatsTest_Success() {
        ResponseEndpointStatsDTO repoResponse = makeResponseEndpoint();
        when(statisticsRepository.findEndpointStats(any(), any(), any()))
                .thenReturn(List.of(repoResponse));

        List<ResponseEndpointStatsDTO> result = statisticsService.getEndpointStats(LocalDateTime.MIN, LocalDateTime.MAX,
                List.of("/events/1"), false);

        assertThat(result.getFirst().getUri(), equalTo(repoResponse.getUri()));
        assertThat(result.getFirst().getApp(), equalTo(repoResponse.getApp()));
        assertThat(result.getFirst().getHits(), equalTo(repoResponse.getHits()));
        verify(statisticsRepository, times(1)).findEndpointStats(any(), any(), any());
    }

    @Test
    public void getEndpointStatsTest_UniqueIP_Success() {
        ResponseEndpointStatsDTO repoResponse = makeResponseEndpoint();
        when(statisticsRepository.findEndpointStatsDistinct(any(), any(), any()))
                .thenReturn(List.of(repoResponse));

        List<ResponseEndpointStatsDTO> result = statisticsService.getEndpointStats(LocalDateTime.MIN, LocalDateTime.MAX,
                List.of("/events/1"), true);

        assertThat(result.getFirst().getUri(), equalTo(repoResponse.getUri()));
        assertThat(result.getFirst().getApp(), equalTo(repoResponse.getApp()));
        assertThat(result.getFirst().getHits(), equalTo(repoResponse.getHits()));
        verify(statisticsRepository, times(1)).findEndpointStatsDistinct(any(), any(), any());
    }

}
