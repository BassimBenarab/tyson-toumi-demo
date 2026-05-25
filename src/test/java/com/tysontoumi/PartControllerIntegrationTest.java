package com.tysontoumi;

import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PartControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PartRepository partRepository;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    @Test
    void getAllParts_returnsEmptyArray() {
        ResponseEntity<Part[]> response = restTemplate.getForEntity("/api/parts", Part[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void createPart_validPart_returns201() {
        Part part = new Part("Testskive", PartCategory.DIAL, "En testskive", 299.0, 5);
        ResponseEntity<Part> response = restTemplate.postForEntity("/api/parts", part, Part.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getName()).isEqualTo("Testskive");
    }

    @Test
    void getPartById_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/parts/999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createPart_thenGetAll_returnsOne() {
        Part part = new Part("Stål kasse", PartCategory.CASE, "Test kasse", 899.0, 3);
        restTemplate.postForEntity("/api/parts", part, Part.class);

        ResponseEntity<Part[]> response = restTemplate.getForEntity("/api/parts", Part[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }
}
