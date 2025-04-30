package com.faspix.controller;

import com.faspix.dto.RequestCompilationDTO;
import com.faspix.dto.ResponseCompilationDTO;
import com.faspix.mapper.CompilationMapper;
import com.faspix.service.CompilationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCompilationDTO createCompilation(
            @RequestBody RequestCompilationDTO compilationDTO
    ) {
        return compilationService.createCompilation(compilationDTO);
    }

    @GetMapping("{compId}")
    public ResponseCompilationDTO findCompilationById(
            @PathVariable Long compId
    ) {
        return compilationService.findCompilationById(compId);
    }

    @GetMapping
    public List<ResponseCompilationDTO> findCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return compilationService.findCompilations(pinned, from, size);
    }

    @PatchMapping("{compId}")
    public ResponseCompilationDTO editCompilation(
            @PathVariable Long compId,
            @RequestBody RequestCompilationDTO compilationDTO
    ) {
        return compilationService.editCompilation(compId, compilationDTO);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(
            @PathVariable Long compId
    ) {
        compilationService.deleteCompilation(compId);
    }

}
