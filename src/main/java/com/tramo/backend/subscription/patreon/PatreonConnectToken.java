package com.tramo.backend.subscription.patreon;

import com.tramo.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

// Maps the OAuth `state` param back to the Tramo user who started the Patreon connect
// flow — the browser redirect round-trip can't carry the Authorization header.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PatreonConnectToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(optional = false)
    private User user;

    private Instant expiresAt;
}
