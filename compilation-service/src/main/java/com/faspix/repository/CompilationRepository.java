package com.faspix.repository;

import com.faspix.entity.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findCompilationsByPinned(Boolean pinned, Pageable pageable);

    Optional<Compilation> findCompilationByTitle (String title);
}
