package com.tramo.backend.project;

import com.tramo.backend.AbstractIntegrationTest;
import com.tramo.backend.project.entity.Project;
import com.tramo.backend.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SnapshotTest extends AbstractIntegrationTest {

    private Project seedProject(User owner, String title) throws Exception {
        return createProject(owner, title, "private", "A description", "java,testing");
    }

    private void setVisibility(User owner, Project project, String visibility) throws Exception {
        mockMvc.perform(put("/api/project/" + pid(project))
                        .header("Authorization", bearer(owner))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"visibility\":\"" + visibility + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void publishingTwiceCreatesIncrementingVersions() throws Exception {
        User owner = createUser("snapowner1");
        Project project = seedProject(owner, "Versioned");

        setVisibility(owner, project, "published");
        setVisibility(owner, project, "private");
        setVisibility(owner, project, "published");

        mockMvc.perform(get("/api/project/" + pid(project) + "/versions").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].version").value(2))
                .andExpect(jsonPath("$[1].version").value(1));
    }

    @Test
    void versionDetailContainsFrozenTrailAndItemContent() throws Exception {
        User owner = createUser("snapowner2");
        Project project = seedProject(owner, "Detailed");
        long trailId = postForId(owner, "/api/project/" + pid(project) + "/trail", """
                {"title":"Trail 0"}""");
        long itemId = postForId(owner, "/api/trail/" + trailId + "/item", """
                {"title":"Item 0"}""");
        mockMvc.perform(put("/api/item/" + itemId + "/content")
                        .header("Authorization", bearer(owner))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Hello frozen world"}"""))
                .andExpect(status().isNoContent());

        setVisibility(owner, project, "published");

        String versionsJson = mockMvc.perform(get("/api/project/" + pid(project) + "/versions")
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long snapshotId = ((Number) com.jayway.jsonpath.JsonPath.read(versionsJson, "$[0].id")).longValue();

        mockMvc.perform(get("/api/project/" + pid(project) + "/versions/" + snapshotId)
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.content.trails[0].title").value("Trail 0"))
                .andExpect(jsonPath("$.content.trails[0].items[0].content").value("Hello frozen world"));

        // Editing after publish doesn't change the frozen version.
        mockMvc.perform(put("/api/item/" + itemId + "/content")
                        .header("Authorization", bearer(owner))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Edited after publish"}"""))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/project/" + pid(project) + "/versions/" + snapshotId)
                        .header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.trails[0].items[0].content").value("Hello frozen world"));
    }

    @Test
    void forkingCreatesUnversionedSnapshotOnSourceProject() throws Exception {
        User owner = createUser("snapowner3");
        User forker = createUser("snapforker3");
        Project project = seedProject(owner, "Forkable");
        setVisibility(owner, project, "published");

        postForProjectId(forker, "/api/project/" + pid(project) + "/fork", "");

        // The fork's snapshot belongs to the source project but isn't a numbered version.
        mockMvc.perform(get("/api/project/" + pid(project) + "/versions").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].version").value(1));
    }

    @Test
    void versionHistoryIsOwnerOnly() throws Exception {
        User owner = createUser("snapowner4");
        User stranger = createUser("snapstranger4");
        Project project = seedProject(owner, "Private History");
        setVisibility(owner, project, "published");

        mockMvc.perform(get("/api/project/" + pid(project) + "/versions").header("Authorization", bearer(stranger)))
                .andExpect(status().isForbidden());
    }

    @Test
    void republishWhileAlreadyPublishedCreatesNewVersion() throws Exception {
        User owner = createUser("snapowner5");
        Project project = seedProject(owner, "Republished");

        setVisibility(owner, project, "published");
        mockMvc.perform(post("/api/project/" + pid(project) + "/publish").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visibility").value("published"));
        mockMvc.perform(post("/api/project/" + pid(project) + "/publish").header("Authorization", bearer(owner)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/project/" + pid(project) + "/versions").header("Authorization", bearer(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].version").value(3))
                .andExpect(jsonPath("$[1].version").value(2))
                .andExpect(jsonPath("$[2].version").value(1));
    }

    @Test
    void votesAndCommentsSurviveRepublish() throws Exception {
        User owner = createUser("snapowner6");
        User fan = createUser("snapfan6");
        Project project = seedProject(owner, "Engaged");
        setVisibility(owner, project, "published");

        mockMvc.perform(post("/api/project/" + pid(project) + "/vote").header("Authorization", bearer(fan)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/project/" + pid(project) + "/comments")
                        .header("Authorization", bearer(fan))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Nice project"}"""))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/project/" + pid(project) + "/publish").header("Authorization", bearer(owner)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/public/project/" + pid(project)).header("Authorization", bearer(fan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteCount").value(1))
                .andExpect(jsonPath("$.commentCount").value(1));
    }

    @Test
    void publicCanReadOldVersionOfPublishedProject() throws Exception {
        User owner = createUser("snapowner7");
        Project project = seedProject(owner, "Linkable");

        setVisibility(owner, project, "published");
        String v1Json = mockMvc.perform(get("/api/project/" + pid(project) + "/versions")
                        .header("Authorization", bearer(owner)))
                .andReturn().getResponse().getContentAsString();
        long v1Id = ((Number) com.jayway.jsonpath.JsonPath.read(v1Json, "$[0].id")).longValue();

        mockMvc.perform(post("/api/project/" + pid(project) + "/publish").header("Authorization", bearer(owner)))
                .andExpect(status().isOk());

        // No Authorization header — this is the public, unauthenticated route.
        mockMvc.perform(get("/api/public/project/" + pid(project) + "/versions/" + v1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));

        // Old versions stay linkable even after the project goes back to private.
        setVisibility(owner, project, "private");
        mockMvc.perform(get("/api/public/project/" + pid(project) + "/versions/" + v1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));
    }

    @Test
    void publicOldVersionDeniedForNeverPublishedProject() throws Exception {
        User owner = createUser("snapowner8");
        Project project = seedProject(owner, "NeverPublished");

        mockMvc.perform(get("/api/public/project/" + pid(project) + "/versions/999"))
                .andExpect(status().isNotFound());
    }
}
