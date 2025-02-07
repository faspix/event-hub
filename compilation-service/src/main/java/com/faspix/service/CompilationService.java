package com.faspix.service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.entity.Compilation;

import java.util.List;

public interface CompilationService {

    Compilation createCompilation(RequestCompilationDTO compilationDTO);

    Compilation findCompilationById(Long id);

    List<Compilation> findCompilations(Integer page, Integer size);

    Compilation editCompilation(Long id, RequestCompilationDTO compilationDTO);

    Boolean deleteCompilation(Long id);

}
