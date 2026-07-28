package com.tramo.backend.subscription.patreon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatreonConnectTokenRepository extends JpaRepository<PatreonConnectToken, UUID> {
    Optional<PatreonConnectToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
