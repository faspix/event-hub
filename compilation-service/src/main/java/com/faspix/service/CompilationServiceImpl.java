package com.faspix.service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.entity.Compilation;
import com.faspix.exception.CompilationNotFountException;
import com.faspix.mapper.CompilationMapper;
import com.faspix.repository.CompilationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.faspix.utility.PageRequestMaker.makePageRequest;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    @Override
    public Compilation createCompilation(RequestCompilationDTO compilationDTO) {
        return compilationRepository.save(
                compilationMapper.requestToCompilation(compilationDTO)
        );
    }

    @Override
    public Compilation findCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new CompilationNotFountException("Compilation with id " + id + " not found")
        );
    }

    @Override
    public List<Compilation> findCompilations(Integer page, Integer size) {
        Pageable pageRequest = makePageRequest(page, size);
        return compilationRepository.findAll(pageRequest)
                .stream()
                .toList();
    }

    @Override
    public Compilation editCompilation(Long id, RequestCompilationDTO compilationDTO) {
        findCompilationById(id);
        Compilation updatedCompilation = compilationMapper.requestToCompilation(compilationDTO);
        updatedCompilation.setId(id);
        return compilationRepository.save(updatedCompilation);
    }

    @Override
    public Boolean deleteCompilation(Long id) {
        findCompilationById(id);
        compilationRepository.deleteById(id);
        return true;
    }
}
