package com.tramo.backend.project;

import com.tramo.backend.AbstractIntegrationTest;
import com.tramo.backend.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileTest extends AbstractIntegrationTest {

    @Test
    void updateProfileSetsBioBirthDateLocationAndWebsite() throws Exception {
        User user = createUser("profileowner");

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bio":"Hello there","birthDate":"1997-12-03","location":"Rosario, Argentina","website":"tramo.app"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Hello there"))
                .andExpect(jsonPath("$.birthDate").value("1997-12-03"))
                .andExpect(jsonPath("$.location").value("Rosario, Argentina"))
                .andExpect(jsonPath("$.website").value("tramo.app"));

        mockMvc.perform(get("/api/profile/me").header("Authorization", bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Hello there"))
                .andExpect(jsonPath("$.birthDate").value("1997-12-03"))
                .andExpect(jsonPath("$.location").value("Rosario, Argentina"))
                .andExpect(jsonPath("$.website").value("tramo.app"));
    }

    @Test
    void partialUpdateDoesNotClobberOtherFields() throws Exception {
        User user = createUser("profilepartial");

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bio":"Original bio","location":"Rosario, Argentina"}"""))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"website":"tramo.app"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("Original bio"))
                .andExpect(jsonPath("$.location").value("Rosario, Argentina"))
                .andExpect(jsonPath("$.website").value("tramo.app"));
    }

    @Test
    void blankOptionalFieldsAreClearedToNull() throws Exception {
        User user = createUser("profileclear");

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bio":"Something","location":"Somewhere","website":"example.com"}"""))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bio":"  ","location":"  ","website":"  "}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value(nullValue()))
                .andExpect(jsonPath("$.location").value(nullValue()))
                .andExpect(jsonPath("$.website").value(nullValue()));
    }

    @Test
    void publicProfileAlwaysExposesLocationAndWebsite() throws Exception {
        User user = createUser("profilepublic");
        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"location":"Rosario, Argentina","website":"tramo.app","birthDate":"1997-12-03"}"""))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/public/users/profilepublic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Rosario, Argentina"))
                .andExpect(jsonPath("$.website").value("tramo.app"));
    }

    @Test
    void publicProfileShowsAgeByDefaultButHidesItWhenOptedOut() throws Exception {
        User user = createUser("profileage");
        mockMvc.perform(put("/api/profile/me")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"birthDate":"1997-12-03"}"""))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/public/users/profileage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").isNumber());

        mockMvc.perform(put("/user/preferences")
                        .header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"showAge":false}"""))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/public/users/profileage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(nullValue()));
    }
}
