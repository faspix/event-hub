package service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.entity.Compilation;
import com.faspix.exception.CompilationNotFountException;
import com.faspix.mapper.CompilationMapper;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.CompilationRepository;
import com.faspix.service.CompilationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ulitity.CompilationFactory.*;

@ExtendWith(MockitoExtension.class)
public class CompilationServiceTest {

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventServiceClient eventServiceClient;

    @Spy
    private CompilationMapper compilationMapper = Mappers.getMapper(CompilationMapper.class);

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Test
    public void createCompilationTest_Success() {
        RequestCompilationDTO compilationDTO = makeRequestCompilation();
        when(compilationRepository.saveAndFlush(any()))
                .thenReturn(makeCompilation());

        ResponseCompilationDTO result = compilationService.createCompilation(compilationDTO);

        assertThat(result.getTitle(), equalTo(compilationDTO.getTitle()));
        assertThat(result.getPinned(), equalTo(compilationDTO.getPinned()));
    }

    @Test
    public void findCompilationByIdTest_Sunccess() {
        Compilation compilation = makeCompilation();
        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(compilation));

        ResponseCompilationDTO result = compilationService.findCompilationById(1L);
        assertThat(result.getId(), equalTo(compilation.getId()));
        assertThat(result.getPinned(), equalTo(compilation.getPinned()));
        assertThat(result.getTitle(), equalTo(compilation.getTitle()));
    }


    @Test
    public void editCompilationTest_Success() {
        Compilation compilation = makeCompilation();
        compilation.setTitle("Updated title");
        RequestCompilationDTO updateDTO = makeRequestCompilation();
        updateDTO.setTitle("Updated title");

        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.of(compilation));
        when(compilationRepository.saveAndFlush(any()))
                .thenReturn(compilation);

        ResponseCompilationDTO result = compilationService.editCompilation(1L, updateDTO);

        assertThat(result.getTitle(), equalTo(updateDTO.getTitle()));
        assertThat(result.getPinned(), equalTo(updateDTO.getPinned()));
    }

    @Test
    public void getAllCompilationsTest_PinnedNull_Success() {
        List<Compilation> compilations = List.of(makeCompilation(), makeCompilation());
        when(compilationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(compilations));

        List<ResponseCompilationDTO> result = compilationService.findCompilations(null, 0, 10);

        assertThat(result.size(), equalTo(compilations.size()));
        assertThat(result.get(0).getTitle(), equalTo(compilations.get(0).getTitle()));
    }


    @Test
    public void getAllCompilationsTest_PinnedTrue_Success() {
        List<Compilation> compilations = List.of(makeCompilation(), makeCompilation());
        when(compilationRepository.findCompilationsByPinned(anyBoolean(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(compilations));

        List<ResponseCompilationDTO> result = compilationService.findCompilations(true, 0, 10);

        assertThat(result.size(), equalTo(compilations.size()));
        assertThat(result.get(0).getTitle(), equalTo(compilations.get(0).getTitle()));
    }

    @Test
    public void deleteCompilationTest_Success() {
        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(makeCompilation()));

        assertDoesNotThrow(
                () -> compilationService.deleteCompilation(1L)
        );
        verify(compilationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteCompilationTest_NotFound() {
        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        CompilationNotFountException exception = assertThrows(CompilationNotFountException.class,
                () -> compilationService.deleteCompilation(1L)
        );
        assertEquals("Compilation with id 1 not found", exception.getMessage());
    }



}
