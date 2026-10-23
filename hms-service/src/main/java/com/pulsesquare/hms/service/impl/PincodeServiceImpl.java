package com.pulsesquare.hms.service.impl;

import com.pulsesquare.hms.model.dto.PincodeDto;
import com.pulsesquare.hms.model.dto.PincodeLookupResponse;
import com.pulsesquare.hms.repo.repository.PincodeRepository;
import com.pulsesquare.hms.service.service.PincodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PincodeServiceImpl implements PincodeService {

    private static final Logger log = LoggerFactory.getLogger(PincodeServiceImpl.class);
    private static final int BATCH_SIZE = 5000;

    private final PincodeRepository pincodeRepository;

    @Autowired
    public PincodeServiceImpl(PincodeRepository pincodeRepository) {
        this.pincodeRepository = pincodeRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        CompletableFuture.runAsync(this::loadPincodesFromCsvIfEmpty);
    }

    @Override
    public Optional<PincodeLookupResponse> lookup(String pincode) {
        return pincodeRepository.findByPincode(pincode);
    }

    @Override
    public List<String> getStates() {
        return pincodeRepository.findDistinctStates();
    }

    @Override
    public List<String> getDistricts(String state) {
        return pincodeRepository.findDistrictsByState(state);
    }

    @Override
    public List<String> getOffices(String state, String district) {
        return pincodeRepository.findOfficesByDistrict(state, district);
    }

    @Override
    public List<PincodeDto> search(String query, int limit) {
        return pincodeRepository.search(query, limit);
    }

    @Override
    public synchronized int loadPincodesFromCsvIfEmpty() {
        try {
            long existingCount = pincodeRepository.count();
            if (existingCount > 0) {
                log.info("Pincode database already initialized with {} records. Skipping CSV import.", existingCount);
                return (int) existingCount;
            }

            log.info("Starting background import of official All-India Pincode directory...");
            InputStream is = null;
            try {
                ClassPathResource resource = new ClassPathResource("pincodes.csv");
                if (resource.exists()) {
                    is = resource.getInputStream();
                }
            } catch (Exception ignored) {
            }

            if (is == null) {
                File localFile = new File("C:/Users/amand/hms-sys/hms-db-dist/src/main/resources/pincodes.csv");
                if (localFile.exists()) {
                    is = new FileInputStream(localFile);
                }
            }

            if (is == null) {
                log.warn("pincodes.csv not found on classpath or filesystem. Pincode table remains empty.");
                return 0;
            }

            int totalLoaded = 0;
            long startTime = System.currentTimeMillis();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String header = reader.readLine(); // skip header line
                if (header == null) return 0;

                List<PincodeDto> batch = new ArrayList<>(BATCH_SIZE);
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    List<String> cols = parseCsvLine(line);
                    if (cols.size() >= 9) {
                        // 0: CircleName, 1: RegionName, 2: DivisionName, 3: OfficeName, 4: Pincode,
                        // 5: OfficeType, 6: Delivery, 7: District, 8: StateName, 9: Latitude, 10: Longitude
                        String circle = clean(cols.get(0));
                        String region = clean(cols.get(1));
                        String division = clean(cols.get(2));
                        String office = clean(cols.get(3));
                        String pincode = clean(cols.get(4));
                        String officeType = clean(cols.get(5));
                        String delivery = clean(cols.get(6));
                        String district = clean(cols.get(7)).toUpperCase();
                        String state = clean(cols.get(8)).toUpperCase();
                        String lat = cols.size() > 9 ? clean(cols.get(9)) : "";
                        String lon = cols.size() > 10 ? clean(cols.get(10)) : "";

                        if (!pincode.isEmpty() && !office.isEmpty()) {
                            batch.add(new PincodeDto(pincode, office, officeType, delivery, division, region, circle, district, state, lat, lon));
                        }
                    }

                    if (batch.size() >= BATCH_SIZE) {
                        pincodeRepository.batchInsert(batch);
                        totalLoaded += batch.size();
                        batch.clear();
                        if (totalLoaded % 25000 == 0) {
                            log.info("Pincode import progress: {} records loaded...", totalLoaded);
                        }
                    }
                }

                if (!batch.isEmpty()) {
                    pincodeRepository.batchInsert(batch);
                    totalLoaded += batch.size();
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Successfully imported {} official Indian PIN code records into database in {} ms.", totalLoaded, elapsed);
            return totalLoaded;

        } catch (Exception e) {
            log.error("Failed to import official pincodes into database: {}", e.getMessage(), e);
            return 0;
        }
    }

    private static String clean(String val) {
        if (val == null) return "";
        String s = val.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values;
    }
}
