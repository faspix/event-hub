package com.faspix.service;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.entity.Compilation;

import java.util.List;

public interface CompilationService {

    ResponseCompilationDTO createCompilation(RequestCompilationDTO compilationDTO);

    ResponseCompilationDTO findCompilationById(Long id);

    List<ResponseCompilationDTO> findCompilations(Boolean pinned, Integer from, Integer size);

    ResponseCompilationDTO editCompilation(Long id, RequestCompilationDTO compilationDTO);

    void checkCompilationExistence(Long id);

    void deleteCompilation(Long id);

}
