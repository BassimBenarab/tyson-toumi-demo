package com.tysontoumi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    @Test
    void getAllParts_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/parts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createPart_validPart_returns201() throws Exception {
        Part part = new Part("Testskive", PartCategory.DIAL, "En testskive", 299.0, 5);

        mockMvc.perform(post("/api/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(part)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Testskive"))
                .andExpect(jsonPath("$.category").value("DIAL"));
    }

    @Test
    void createPart_missingName_returns400() throws Exception {
        Part part = new Part("", PartCategory.DIAL, "Ingen navn", 299.0, 5);

        mockMvc.perform(post("/api/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(part)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPartById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/parts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPartsGrouped_returnsGroupedByCategory() throws Exception {
        Part dial = new Part("Hvid skive", PartCategory.DIAL, "Test", 299.0, 5);
        Part strap = new Part("Lænke", PartCategory.STRAP, "Test", 199.0, 3);
        partRepository.save(dial);
        partRepository.save(strap);

        mockMvc.perform(get("/api/parts/grouped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.DIAL").isArray())
                .andExpect(jsonPath("$.STRAP").isArray());
    }
}
