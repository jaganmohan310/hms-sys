package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRefreshTokens;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<HmsRefreshTokens> findByToken(String token);

    HmsRefreshTokens save(HmsRefreshTokens token);

    int deleteByUserId(Long userId);
}
