package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRefreshTokens;
import com.pulsesquare.hms.repo.repository.RefreshTokenRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.pulsesquare.hms.jooq.Tables.HMS_REFRESH_TOKENS;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final DSLContext dsl;

    @Autowired
    public RefreshTokenRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<HmsRefreshTokens> findByToken(String token) {
        if (token == null) return Optional.empty();

        HmsRefreshTokens record = dsl.selectFrom(HMS_REFRESH_TOKENS)
                .where(HMS_REFRESH_TOKENS.TOKEN.eq(token))
                .fetchOneInto(HmsRefreshTokens.class);

        return Optional.ofNullable(record);
    }

    @Override
    public HmsRefreshTokens save(HmsRefreshTokens token) {
        LocalDateTime now = LocalDateTime.now();

        if (token.getId() == null) {
            if (token.getCreatedAt() == null) {
                token.setCreatedAt(now);
            }

            Long generatedId = null;
            try {
                org.jooq.Record1<Long> result = dsl.insertInto(HMS_REFRESH_TOKENS)
                        .columns(HMS_REFRESH_TOKENS.TOKEN, HMS_REFRESH_TOKENS.USER_ID,
                                HMS_REFRESH_TOKENS.EXPIRY_DATE, HMS_REFRESH_TOKENS.REVOKED, HMS_REFRESH_TOKENS.CREATED_AT)
                        .values(token.getToken(), token.getUserId(), token.getExpiryDate(), token.getRevoked(), token.getCreatedAt())
                        .returningResult(HMS_REFRESH_TOKENS.ID)
                        .fetchOne();

                if (result != null) {
                    generatedId = result.value1();
                }
            } catch (Exception ex) {
                dsl.insertInto(HMS_REFRESH_TOKENS)
                        .columns(HMS_REFRESH_TOKENS.TOKEN, HMS_REFRESH_TOKENS.USER_ID,
                                HMS_REFRESH_TOKENS.EXPIRY_DATE, HMS_REFRESH_TOKENS.REVOKED, HMS_REFRESH_TOKENS.CREATED_AT)
                        .values(token.getToken(), token.getUserId(), token.getExpiryDate(), token.getRevoked(), token.getCreatedAt())
                        .execute();
            }

            if (generatedId == null) {
                generatedId = dsl.select(HMS_REFRESH_TOKENS.ID)
                        .from(HMS_REFRESH_TOKENS)
                        .where(HMS_REFRESH_TOKENS.TOKEN.eq(token.getToken()))
                        .fetchOneInto(Long.class);
            }
            token.setId(generatedId);
        } else {
            dsl.update(HMS_REFRESH_TOKENS)
                    .set(HMS_REFRESH_TOKENS.REVOKED, token.getRevoked())
                    .where(HMS_REFRESH_TOKENS.ID.eq(token.getId()))
                    .execute();
        }

        return token;
    }

    @Override
    public int deleteByUserId(Long userId) {
        return dsl.deleteFrom(HMS_REFRESH_TOKENS)
                .where(HMS_REFRESH_TOKENS.USER_ID.eq(userId))
                .execute();
    }
}
