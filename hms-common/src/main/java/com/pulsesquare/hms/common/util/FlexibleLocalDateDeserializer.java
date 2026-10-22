package com.pulsesquare.hms.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Enterprise Flexible LocalDate Deserializer.
 * Leniently parses ISO (yyyy-MM-dd), Indian/European (dd/MM/yyyy, dd-MM-yyyy),
 * ISO DateTimes, and treats empty/whitespace strings as null.
 */
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE,                    // 2026-10-22
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),          // 22/10/2026
            DateTimeFormatter.ofPattern("d/M/yyyy"),            // 2/5/2026
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),          // 22-10-2026
            DateTimeFormatter.ofPattern("d-M-yyyy"),            // 2-5-2026
            DateTimeFormatter.ofPattern("yyyy/MM/dd")           // 2026/10/22
    };

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return null;
        }

        String trimmed = value.trim();

        // Handle ISO DateTime format (e.g. 2026-10-22T13:30:00.000Z)
        if (trimmed.contains("T")) {
            trimmed = trimmed.substring(0, trimmed.indexOf("T"));
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }

        return null;
    }
}
