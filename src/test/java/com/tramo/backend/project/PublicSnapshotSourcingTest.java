package com.tramo.backend.project;

import com.tramo.backend.AbstractIntegrationTest;
import com.tramo.backend.project.entity.Project;
import com.tramo.backend.project.service.ProjectService;
import com.tramo.backend.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Explore + the public project page now source published content from the latest PUBLISH
// snapshot instead of live tables (ProjectService.getPublicProject / toFeedItem), so that
// editing after publishing doesn't change what's already public until the next publish.
class PublicSnapshotSourcingTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectService projectService;

    private void setVisibility(User owner, Project project, String visibility) throws Exception {
        mockMvc.perform(put("/api/project/" + pid(project))
                        .header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visibility\":\"" + visibility + "\"}"))
                .andExpect(status().isOk());
    }

    private void renameProject(User owner, Project project, String newTitle) throws Exception {
        mockMvc.perform(put("/api/project/" + pid(project))
                        .header("Authorization", bearer(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + newTitle + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void publicProjectPageShowsFrozenContentAfterLiveEdit() throws Exception {
        User owner = createUser("snapsrcowner1");
        Project project = createProject(owner, "Original Title", "private", "Original description", "tag");
        setVisibility(owner, project, "published");

        renameProject(owner, project, "Edited Title Live");

        mockMvc.perform(get("/api/public/project/" + pid(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original Title"));
    }

    @Test
    void exploreFeedShowsFrozenContentAfterLiveEdit() throws Exception {
        User owner = createUser("snapsrcowner2");
        Project project = createProject(owner, "Explore Original", "private", "Original description", "tag");
        setVisibility(owner, project, "published");

        renameProject(owner, project, "Explore Edited Live");

        mockMvc.perform(get("/api/public/explore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feed[?(@.title=='Explore Original')]").exists())
                .andExpect(jsonPath("$.feed[?(@.title=='Explore Edited Live')]").doesNotExist());
    }

    @Test
    void unlistedProjectStaysLive() throws Exception {
        User owner = createUser("snapsrcowner3");
        Project project = createProject(owner, "Unlisted Original", "private", "Original description", "tag");
        setVisibility(owner, project, "unlisted");

        renameProject(owner, project, "Unlisted Edited Live");

        mockMvc.perform(get("/api/public/project/" + pid(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Unlisted Edited Live"));
    }

    @Test
    void backfillCreatesSnapshotForPreExistingPublishedProject() throws Exception {
        User owner = createUser("snapsrcowner4");
        // Bypasses the real publish flow entirely — simulates a project published before
        // snapshots existed, exactly what ProjectSnapshotBackfillRunner is meant to fix.
        Project project = createProject(owner, "Legacy Published", "published", "Legacy description", "tag");

        projectService.backfillMissingPublishSnapshots();

        renameProject(owner, project, "Legacy Edited After Backfill");

        mockMvc.perform(get("/api/public/project/" + pid(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Legacy Published"));
    }
}
