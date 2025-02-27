package com.faspix.service;

import com.faspix.client.EventServiceClient;
import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.dto.ResponseEventDTO;
import com.faspix.dto.ResponseEventShortDTO;
import com.faspix.entity.Compilation;
import com.faspix.exception.CompilationAlreadyExistException;
import com.faspix.exception.CompilationNotFountException;
import com.faspix.mapper.CompilationMapper;
import com.faspix.mapper.EventMapper;
import com.faspix.repository.CompilationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    private final EventMapper eventMapper;

    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseCompilationDTO createCompilation(RequestCompilationDTO compilationDTO) {

        Compilation compilation;
        try {
            compilation = compilationRepository.saveAndFlush(
                    compilationMapper.requestToCompilation(compilationDTO)
            );
        } catch (DataIntegrityViolationException e) {
            throw new CompilationAlreadyExistException(
                    "Compilation with title '" + compilationDTO.getTitle() + "' already exist");
        }
        return getResponseDTO(compilation);
    }

    @Override
    public ResponseCompilationDTO findCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new CompilationNotFountException("Compilation with id " + id + " not found")
        );
        return getResponseDTO(compilation);
    }

    @Override
    public List<ResponseCompilationDTO> findCompilations(Boolean pinned, Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);

        Page<Compilation> compilations = (pinned == null)
                ? compilationRepository.findAll(pageRequest)
                : compilationRepository.findCompilationsByPinned(pinned, pageRequest);

        return compilations.stream()
                .map(this::getResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseCompilationDTO editCompilation(Long id, RequestCompilationDTO compilationDTO) {
        findCompilationById(id);
        Compilation updatedCompilation = compilationMapper.requestToCompilation(compilationDTO);
        updatedCompilation.setId(id);
        Compilation compilation;
        try {
            compilation = compilationRepository.saveAndFlush(updatedCompilation);
        } catch (DataIntegrityViolationException e) {
            throw new CompilationAlreadyExistException(
                    "Compilation with title '" + compilationDTO.getTitle() + "' already exist");
        }
        return getResponseDTO(compilation);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Boolean deleteCompilation(Long id) {
        findCompilationById(id);
        compilationRepository.deleteById(id);
        return true;
    }

    private ResponseCompilationDTO getResponseDTO(Compilation compilation) {
        List<ResponseEventShortDTO> events = getEventsFromEventIds(compilation.getEvents());
        ResponseCompilationDTO dto = compilationMapper.compilationToResponse(compilation);
        dto.setEvents(events);
        return dto;
    }

    private List<ResponseEventShortDTO> getEventsFromEventIds(List<Long> eventIds) {
        return eventIds.stream()
                .map(eventServiceClient::getEventById)
                .map(eventMapper::eventToShortEvent)
                .toList();
    }
}
