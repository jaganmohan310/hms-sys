package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.jooq.Sequences;
import com.pulsesquare.hms.jooq.tables.pojos.HmsPatients;
import com.pulsesquare.hms.jooq.tables.records.HmsPatientsRecord;
import com.pulsesquare.hms.repo.repository.PatientRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.pulsesquare.hms.jooq.Tables.HMS_PATIENTS;

@Repository
public class PatientRepositoryImpl implements PatientRepository {

    private final DSLContext dsl;

    @Autowired
    public PatientRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<HmsPatients> findById(Long id) {
        if (id == null) return Optional.empty();
        HmsPatients record = dsl.selectFrom(HMS_PATIENTS)
                .where(HMS_PATIENTS.ID.eq(id))
                .fetchOneInto(HmsPatients.class);

        return Optional.ofNullable(record);
    }

    @Override
    public Optional<HmsPatients> findByMrn(String mrn) {
        if (mrn == null || mrn.trim().isEmpty()) return Optional.empty();

        HmsPatients record = dsl.selectFrom(HMS_PATIENTS)
                .where(HMS_PATIENTS.MRN.equalIgnoreCase(mrn.trim()))
                .fetchOneInto(HmsPatients.class);

        return Optional.ofNullable(record);
    }

    @Override
    public boolean existsByMrn(String mrn) {
        if (mrn == null || mrn.trim().isEmpty()) return false;
        return dsl.fetchExists(
                dsl.selectFrom(HMS_PATIENTS)
                        .where(HMS_PATIENTS.MRN.equalIgnoreCase(mrn.trim()))
        );
    }

    @Override
    public List<HmsPatients> search(String query, int page, int size) {
        Condition condition = buildSearchCondition(query);
        int limit = size > 0 ? size : 20;
        int offset = Math.max(0, page) * limit;

        return dsl.selectFrom(HMS_PATIENTS)
                .where(condition)
                .orderBy(HMS_PATIENTS.ID.desc())
                .limit(limit)
                .offset(offset)
                .fetchInto(HmsPatients.class);
    }

    @Override
    public long countSearch(String query) {
        Condition condition = buildSearchCondition(query);
        return dsl.fetchCount(
                dsl.selectFrom(HMS_PATIENTS).where(condition)
        );
    }

    @Override
    public String generateNextMrn() {
        Long nextVal;
        try {
            nextVal = dsl.nextval(Sequences.SEQ_HMS_MRN);
        } catch (Exception e) {
            // Safe fallback if sequence is not present
            Long maxId = dsl.select(DSL.max(HMS_PATIENTS.ID)).from(HMS_PATIENTS).fetchOneInto(Long.class);
            nextVal = (maxId != null ? maxId : 0L) + 10001L;
        }

        if (nextVal == null) {
            nextVal = 10001L;
        }
        return String.format("MRN-%d-%06d", LocalDate.now().getYear(), nextVal);
    }

    @Override
    public HmsPatients save(HmsPatients patient) {
        if (patient == null) return null;

        LocalDateTime now = LocalDateTime.now();

        if (patient.getId() == null) {
            if (patient.getMrn() == null || patient.getMrn().trim().isEmpty()) {
                patient.setMrn(generateNextMrn());
            }

            HmsPatientsRecord record = dsl.newRecord(HMS_PATIENTS);
            record.setMrn(patient.getMrn().trim());
            record.setTitle(patient.getTitle() != null ? patient.getTitle() : "Mr");
            record.setFirstName(patient.getFirstName() != null ? patient.getFirstName().trim() : "");
            record.setMiddleName(patient.getMiddleName());
            record.setLastName(patient.getLastName() != null ? patient.getLastName().trim() : "");
            record.setGender(patient.getGender() != null ? patient.getGender().trim().toUpperCase() : "OTHER");
            record.setDateOfBirth(patient.getDateOfBirth());
            record.setAgeYears(patient.getAgeYears());
            record.setAgeMonths(patient.getAgeMonths());
            record.setAgeDays(patient.getAgeDays());
            record.setBloodGroup(patient.getBloodGroup());
            record.setContactNumber(patient.getContactNumber());
            record.setEmail(patient.getEmail());
            record.setMaritalStatus(patient.getMaritalStatus());
            record.setReligion(patient.getReligion());
            record.setAddress(patient.getAddress());
            record.setCity(patient.getCity());
            record.setState(patient.getState());
            record.setCountry(patient.getCountry() != null ? patient.getCountry() : "INDIA");
            record.setZipCode(patient.getZipCode());
            record.setReferralDoctor(patient.getReferralDoctor());
            record.setDepartment(patient.getDepartment() != null ? patient.getDepartment() : "Out-Patient");
            record.setRegistrationFee(patient.getRegistrationFee());
            record.setPaymentType(patient.getPaymentType() != null ? patient.getPaymentType() : "Cash");
            record.setActive(patient.getActive() != null ? patient.getActive() : true);
            record.setRegisteredAt(patient.getRegisteredAt() != null ? patient.getRegisteredAt() : now);
            record.setCreatedAt(now);
            record.setUpdatedAt(now);

            record.store();
            return record.into(HmsPatients.class);
        } else {
            HmsPatientsRecord record = dsl.fetchOne(HMS_PATIENTS, HMS_PATIENTS.ID.eq(patient.getId()));
            if (record != null) {
                if (patient.getMrn() != null) record.setMrn(patient.getMrn().trim());
                if (patient.getTitle() != null) record.setTitle(patient.getTitle());
                if (patient.getFirstName() != null) record.setFirstName(patient.getFirstName().trim());
                if (patient.getMiddleName() != null) record.setMiddleName(patient.getMiddleName());
                if (patient.getLastName() != null) record.setLastName(patient.getLastName().trim());
                if (patient.getGender() != null) record.setGender(patient.getGender().trim().toUpperCase());
                if (patient.getDateOfBirth() != null) record.setDateOfBirth(patient.getDateOfBirth());
                if (patient.getAgeYears() != null) record.setAgeYears(patient.getAgeYears());
                if (patient.getAgeMonths() != null) record.setAgeMonths(patient.getAgeMonths());
                if (patient.getAgeDays() != null) record.setAgeDays(patient.getAgeDays());
                if (patient.getBloodGroup() != null) record.setBloodGroup(patient.getBloodGroup());
                if (patient.getContactNumber() != null) record.setContactNumber(patient.getContactNumber());
                if (patient.getEmail() != null) record.setEmail(patient.getEmail());
                if (patient.getMaritalStatus() != null) record.setMaritalStatus(patient.getMaritalStatus());
                if (patient.getReligion() != null) record.setReligion(patient.getReligion());
                if (patient.getAddress() != null) record.setAddress(patient.getAddress());
                if (patient.getCity() != null) record.setCity(patient.getCity());
                if (patient.getState() != null) record.setState(patient.getState());
                if (patient.getCountry() != null) record.setCountry(patient.getCountry());
                if (patient.getZipCode() != null) record.setZipCode(patient.getZipCode());
                if (patient.getReferralDoctor() != null) record.setReferralDoctor(patient.getReferralDoctor());
                if (patient.getDepartment() != null) record.setDepartment(patient.getDepartment());
                if (patient.getRegistrationFee() != null) record.setRegistrationFee(patient.getRegistrationFee());
                if (patient.getPaymentType() != null) record.setPaymentType(patient.getPaymentType());
                if (patient.getActive() != null) record.setActive(patient.getActive());
                record.setUpdatedAt(now);

                record.store();
                return record.into(HmsPatients.class);
            }
            return patient;
        }
    }

    private Condition buildSearchCondition(String query) {
        if (query == null || query.trim().isEmpty()) {
            return DSL.noCondition();
        }
        String q = "%" + query.trim().toLowerCase() + "%";
        return DSL.lower(HMS_PATIENTS.MRN).like(q)
                .or(DSL.lower(HMS_PATIENTS.CONTACT_NUMBER).like(q))
                .or(DSL.lower(HMS_PATIENTS.FIRST_NAME).like(q))
                .or(DSL.lower(HMS_PATIENTS.LAST_NAME).like(q));
    }
}
