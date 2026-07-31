package com.tramo.backend.subscription.patreon;

import com.tramo.backend.AbstractIntegrationTest;
import com.tramo.backend.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class PatreonWebhookTest extends AbstractIntegrationTest {

    private String sign(String body) throws Exception {
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(new SecretKeySpec(TEST_PATREON_WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacMD5"));
        return HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
    }

    private String pledgePayload(String patreonUserId) {
        return """
                {"data":{"id":"member-1","type":"member","relationships":{"user":{"data":{"id":"%s","type":"user"}}}}}"""
                .formatted(patreonUserId);
    }

    @Test
    void activatesSupporterSubscriptionForLinkedUserOnPledgeCreate() throws Exception {
        User user = createUser("webhookpatron");
        user.setPatreonUserId("patreon-789");
        userRepository.save(user);

        String body = pledgePayload("patreon-789");
        mockMvc.perform(post("/api/webhooks/patreon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Patreon-Event", "members:pledge:create")
                        .header("X-Patreon-Signature", sign(body))
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/subscription").header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supporter").value(true));
    }

    @Test
    void deactivatesSupporterSubscriptionOnPledgeDelete() throws Exception {
        User user = createUser("lapsingpatron");
        user.setPatreonUserId("patreon-999");
        userRepository.save(user);
        String body = pledgePayload("patreon-999");

        mockMvc.perform(post("/api/webhooks/patreon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Patreon-Event", "members:pledge:create")
                        .header("X-Patreon-Signature", sign(body))
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/webhooks/patreon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Patreon-Event", "members:pledge:delete")
                        .header("X-Patreon-Signature", sign(body))
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/subscription").header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supporter").value(false));
    }

    @Test
    void rejectsBadSignature() throws Exception {
        String body = pledgePayload("patreon-000");
        mockMvc.perform(post("/api/webhooks/patreon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Patreon-Event", "members:pledge:create")
                        .header("X-Patreon-Signature", "deadbeef")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void ignoresPledgeForUnlinkedPatreonUser() throws Exception {
        String body = pledgePayload("patreon-unknown");
        mockMvc.perform(post("/api/webhooks/patreon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Patreon-Event", "members:pledge:create")
                        .header("X-Patreon-Signature", sign(body))
                        .content(body))
                .andExpect(status().isOk());
    }
}
