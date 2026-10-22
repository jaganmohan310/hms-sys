package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.model.dto.PincodeDto;
import com.pulsesquare.hms.model.dto.PincodeLookupResponse;
import com.pulsesquare.hms.repo.repository.PincodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.*;

@Repository
public class PincodeRepositoryImpl implements PincodeRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PincodeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<PincodeLookupResponse> findByPincode(String pincode) {
        if (pincode == null || pincode.trim().isEmpty()) {
            return Optional.empty();
        }
        String cleanPin = pincode.trim();
        String sql = "SELECT pincode, office_name, division_name, district, state_name FROM hms_pincodes WHERE pincode = ? ORDER BY id";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, cleanPin);
        if (rows.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> first = rows.get(0);
        String state = Objects.toString(first.get("STATE_NAME"), "");
        String district = Objects.toString(first.get("DISTRICT"), "");
        String division = Objects.toString(first.get("DIVISION_NAME"), "");
        String primaryTaluk = (division != null && !division.isEmpty()) ? division.replaceAll("(?i)\\s*Division$", "").trim() : district;

        List<String> offices = new ArrayList<>();
        Set<String> uniqueOffices = new LinkedHashSet<>();
        List<String> areas = new ArrayList<>();
        Set<String> uniqueAreas = new LinkedHashSet<>();

        if (primaryTaluk != null && !primaryTaluk.trim().isEmpty()) {
            if (uniqueAreas.add(primaryTaluk.trim())) {
                areas.add(primaryTaluk.trim());
            }
        }

        for (Map<String, Object> row : rows) {
            String off = Objects.toString(row.get("OFFICE_NAME"), "").trim();
            if (!off.isEmpty() && uniqueOffices.add(off)) {
                offices.add(off);
                String clean = off.replaceAll("(?i)\\s+(S\\.O|B\\.O|H\\.O|P\\.O|PO|SO|BO|HO)$", "").trim();
                if (!clean.isEmpty() && uniqueAreas.add(clean)) {
                    areas.add(clean);
                }
                if (uniqueAreas.add(off)) {
                    areas.add(off);
                }
            }
        }

        if (areas.isEmpty() && !district.isEmpty()) {
            areas.add(district);
        }

        return Optional.of(new PincodeLookupResponse(cleanPin, state, district, primaryTaluk, offices, areas));
    }

    @Override
    public List<String> findDistinctStates() {
        String sql = "SELECT DISTINCT state_name FROM hms_pincodes WHERE state_name IS NOT NULL AND state_name <> '' ORDER BY state_name";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<String> findDistrictsByState(String state) {
        if (state == null || state.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT DISTINCT district FROM hms_pincodes WHERE UPPER(state_name) = UPPER(?) AND district IS NOT NULL AND district <> '' ORDER BY district";
        return jdbcTemplate.queryForList(sql, String.class, state.trim());
    }

    @Override
    public List<String> findOfficesByDistrict(String state, String district) {
        if (district == null || district.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String sql;
        List<String> results;
        if (state != null && !state.trim().isEmpty()) {
            sql = "SELECT DISTINCT office_name FROM hms_pincodes WHERE UPPER(state_name) = UPPER(?) AND UPPER(district) = UPPER(?) AND office_name IS NOT NULL AND office_name <> '' ORDER BY office_name";
            results = jdbcTemplate.queryForList(sql, String.class, state.trim(), district.trim());
        } else {
            sql = "SELECT DISTINCT office_name FROM hms_pincodes WHERE UPPER(district) = UPPER(?) AND office_name IS NOT NULL AND office_name <> '' ORDER BY office_name";
            results = jdbcTemplate.queryForList(sql, String.class, district.trim());
        }
        return results;
    }

    @Override
    public List<PincodeDto> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        int max = limit > 0 ? Math.min(limit, 100) : 25;
        String term = "%" + query.trim().toUpperCase() + "%";
        String sql = "SELECT pincode, office_name, office_type, delivery_status, division_name, region_name, circle_name, district, state_name, latitude, longitude " +
                     "FROM hms_pincodes WHERE pincode LIKE ? OR UPPER(office_name) LIKE ? OR UPPER(district) LIKE ? FETCH FIRST " + max + " ROWS ONLY";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PincodeDto(
                rs.getString("pincode"),
                rs.getString("office_name"),
                rs.getString("office_type"),
                rs.getString("delivery_status"),
                rs.getString("division_name"),
                rs.getString("region_name"),
                rs.getString("circle_name"),
                rs.getString("district"),
                rs.getString("state_name"),
                rs.getString("latitude"),
                rs.getString("longitude")
        ), term, term, term);
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM hms_pincodes";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public void batchInsert(List<PincodeDto> batch) {
        if (batch == null || batch.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO hms_pincodes (pincode, office_name, office_type, delivery_status, division_name, region_name, circle_name, district, state_name, latitude, longitude) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (PreparedStatement ps, PincodeDto p) -> {
            ps.setString(1, p.getPincode());
            ps.setString(2, p.getOfficeName());
            ps.setString(3, p.getOfficeType());
            ps.setString(4, p.getDeliveryStatus());
            ps.setString(5, p.getDivisionName());
            ps.setString(6, p.getRegionName());
            ps.setString(7, p.getCircleName());
            ps.setString(8, p.getDistrict());
            ps.setString(9, p.getStateName());
            ps.setString(10, p.getLatitude());
            ps.setString(11, p.getLongitude());
        });
    }
}
