package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.jooq.tables.pojos.HmsPatients;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Optional<HmsPatients> findById(Long id);

    Optional<HmsPatients> findByMrn(String mrn);

    boolean existsByMrn(String mrn);

    List<HmsPatients> search(String query, int page, int size);

    long countSearch(String query);

    String generateNextMrn();

    HmsPatients save(HmsPatients patient);

    boolean deleteByMrn(String mrn);
}
