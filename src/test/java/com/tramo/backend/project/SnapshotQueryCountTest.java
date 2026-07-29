package com.tramo.backend.project;

import com.tramo.backend.AbstractIntegrationTest;
import com.tramo.backend.project.entity.Project;
import com.tramo.backend.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Same statistics-based approach as project/QueryCountTest — publishing/forking now also
// writes a ProjectSnapshot (see ProjectService.createSnapshot), which walks every trail/item.
class SnapshotQueryCountTest extends AbstractIntegrationTest {

    private Project seedUnpublishedProject(User owner, String title, int trails, int itemsPerTrail) throws Exception {
        Project project = createProject(owner, title, "private", "A description", "java,testing");
        for (int t = 0; t < trails; t++) {
            long trailId = postForId(owner, "/api/project/" + pid(project) + "/trail", """
                    {"title":"Trail %d"}""".formatted(t));
            for (int i = 0; i < itemsPerTrail; i++) {
                postForId(owner, "/api/trail/" + trailId + "/item", """
                        {"title":"Item %d"}""".formatted(i));
            }
        }
        return project;
    }

    private void publish(User owner, Project project) throws Exception {
        mockMvc.perform(put("/api/project/" + pid(project))
                        .header("Authorization", bearer(owner))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"visibility":"published"}"""))
                .andExpect(status().isOk());
    }

    @Test
    void publishQueryCountDoesNotScaleWithItemCount() throws Exception {
        // Separate owners: checkAndAwardBadges only does its full (first-ever-publish) work
        // once per user, which would otherwise make the second call look artificially cheaper.
        User smallOwner = createUser("qcsnapowner1");
        User largeOwner = createUser("qcsnapowner2");
        Project small = seedUnpublishedProject(smallOwner, "Snap Small", 2, 1);
        Project large = seedUnpublishedProject(largeOwner, "Snap Large", 2, 8);

        long smallCount = queryCount(() -> publish(smallOwner, small));
        long largeCount = queryCount(() -> publish(largeOwner, large));

        // Unlike the read-only guards below, this path INSERTs a new ProjectSnapshot row —
        // occasionally Hibernate's id-sequence generator fetches a fresh batch mid-call, adding
        // one extra query on whichever side happens to hit that boundary (verified in isolation
        // to be unrelated to item count: identical counts across repeated standalone runs). A
        // real N+1 here would scale with item count (7+ extra queries for 8 vs 1 items), not ±1.
        assertThat(largeCount).isCloseTo(smallCount, org.assertj.core.data.Offset.offset(2L));
    }

    @Test
    void versionsListQueryCountDoesNotScaleWithVersionCount() throws Exception {
        User owner = createUser("qcsnapowner2");
        Project project = seedUnpublishedProject(owner, "Snap Versions", 1, 1);
        publish(owner, project);

        long smallCount = queryCount(() -> mockMvc.perform(get("/api/project/" + pid(project) + "/versions")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk()));

        for (int i = 0; i < 6; i++) {
            mockMvc.perform(put("/api/project/" + pid(project))
                            .header("Authorization", bearer(owner))
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("""
                                    {"visibility":"private"}"""))
                    .andExpect(status().isOk());
            publish(owner, project);
        }

        long largeCount = queryCount(() -> mockMvc.perform(get("/api/project/" + pid(project) + "/versions")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk()));

        assertThat(largeCount).isEqualTo(smallCount);
    }
}
