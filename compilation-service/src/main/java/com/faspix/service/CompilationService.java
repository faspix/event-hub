package com.faspix.service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.entity.Compilation;

import java.util.List;

public interface CompilationService {

    ResponseCompilationDTO createCompilation(RequestCompilationDTO compilationDTO);

    Compilation findCompilationById(Long id);

    List<Compilation> findCompilations(Boolean pinned, Integer page, Integer size);

    Compilation editCompilation(Long id, RequestCompilationDTO compilationDTO);

    Boolean deleteCompilation(Long id);

}
