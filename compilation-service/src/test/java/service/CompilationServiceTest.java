package service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.dto.ResponseCompilationShortDTO;
import com.faspix.entity.Compilation;
import com.faspix.exception.CompilationNotFoundException;
import com.faspix.mapper.CompilationMapper;
import com.faspix.repository.CompilationRepository;
import com.faspix.service.CompilationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
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
    private CompilationService compilationService;

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private CacheManager cacheManager;

    @Spy
    private final CompilationMapper compilationMapper = Mappers.getMapper(CompilationMapper.class);

    @Test
    public void createCompilationTest_Success() {
        RequestCompilationDTO compilationDTO = makeRequestCompilation();
        when(compilationRepository.saveAndFlush(any()))
                .thenReturn(makeCompilation());

        ResponseCompilationDTO result = compilationService.createCompilation(compilationDTO);

        assertThat(result.title(), equalTo(compilationDTO.getTitle()));
        assertThat(result.pinned(), equalTo(compilationDTO.getPinned()));
    }

    @Test
    public void findCompilationByIdTest_Sunccess() {
        Compilation compilation = makeCompilation();
        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(compilation));

        ResponseCompilationDTO result = compilationService.findCompilationById(1L);
        assertThat(result.id(), equalTo(compilation.getId()));
        assertThat(result.pinned(), equalTo(compilation.getPinned()));
        assertThat(result.title(), equalTo(compilation.getTitle()));
    }

    @Test
    public void editCompilationTest_Success() {
        Compilation compilation = makeCompilation();
        compilation.setTitle("Updated title");
        RequestCompilationDTO updateDTO = makeRequestCompilation();
        updateDTO.setTitle("Updated title");

        when(compilationRepository.existsById(anyLong()))
                .thenReturn(true);
        when(compilationRepository.saveAndFlush(any()))
                .thenReturn(compilation);

        ResponseCompilationDTO result = compilationService.editCompilation(1L, updateDTO);

        assertThat(result.title(), equalTo(updateDTO.getTitle()));
        assertThat(result.pinned(), equalTo(updateDTO.getPinned()));
    }

    @Test
    public void getAllCompilationsTest_PinnedNull_Success() {
        List<Compilation> compilations = List.of(makeCompilation(), makeCompilation());
        when(compilationRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(compilations));

        List<ResponseCompilationShortDTO> result = compilationService.findCompilations(null, 0, 10);

        assertThat(result.size(), equalTo(compilations.size()));
        assertThat(result.get(0).title(), equalTo(compilations.get(0).getTitle()));
    }


    @Test
    public void getAllCompilationsTest_PinnedTrue_Success() {
        List<Compilation> compilations = List.of(makeCompilation(), makeCompilation());
        when(compilationRepository.findCompilationsByPinned(anyBoolean(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(compilations));

        List<ResponseCompilationShortDTO> result = compilationService.findCompilations(true, 0, 10);

        assertThat(result.size(), equalTo(compilations.size()));
        assertThat(result.get(0).title(), equalTo(compilations.get(0).getTitle()));
    }

    @Test
    public void deleteCompilationTest_Success() {
        when(compilationRepository.existsById(anyLong()))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> compilationService.deleteCompilation(1L)
        );
        verify(compilationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteCompilationTest_NotFound() {
        when(compilationRepository.existsById(anyLong()))
                .thenReturn(false);

        CompilationNotFoundException exception = assertThrows(CompilationNotFoundException.class,
                () -> compilationService.deleteCompilation(1L)
        );
        assertEquals("Compilation with id 1 not found", exception.getMessage());
    }



}
