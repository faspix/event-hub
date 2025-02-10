package com.faspix.service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.entity.Compilation;
import com.faspix.exception.CompilationAlreadyExistException;
import com.faspix.exception.CompilationNotFountException;
import com.faspix.mapper.CompilationMapper;
import com.faspix.repository.CompilationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public Compilation createCompilation(RequestCompilationDTO compilationDTO) {
        try {
            return compilationRepository.saveAndFlush(
                    compilationMapper.requestToCompilation(compilationDTO)
            );
        } catch (DataIntegrityViolationException e) {
            throw new CompilationAlreadyExistException(
                    "Compilation with title '" + compilationDTO.getTitle() + "' already exist");
        }
    }

    @Override
    public Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new CompilationNotFountException("Compilation with id " + id + " not found")
        );
    }

    @Override
    public List<Compilation> findCompilations(Boolean pinned, Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);
        if (pinned == null)
            return compilationRepository.findAll(pageRequest)
                    .stream()
                    .toList();
        else
            return compilationRepository.findCompilationsByPinned(pinned, pageRequest);
    }

    @Override
    @Transactional
    public Compilation editCompilation(Long id, RequestCompilationDTO compilationDTO) {
        findCompilationById(id);
        Compilation updatedCompilation = compilationMapper.requestToCompilation(compilationDTO);
        updatedCompilation.setId(id);
        try {
            return compilationRepository.saveAndFlush(updatedCompilation);
        } catch (DataIntegrityViolationException e) {
            throw new CompilationAlreadyExistException(
                    "Compilation with title '" + compilationDTO.getTitle() + "' already exist");
        }
    }

    @Override
    @Transactional
    public Boolean deleteCompilation(Long id) {
        findCompilationById(id);
        compilationRepository.deleteById(id);
        return true;
    }
}
