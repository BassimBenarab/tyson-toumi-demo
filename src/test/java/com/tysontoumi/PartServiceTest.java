package com.tysontoumi;

import com.tysontoumi.exception.ResourceNotFoundException;
import com.tysontoumi.model.Part;
import com.tysontoumi.model.PartCategory;
import com.tysontoumi.repository.PartRepository;
import com.tysontoumi.service.PartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @InjectMocks
    private PartService partService;

    private Part testPart;

    @BeforeEach
    void setUp() {
        testPart = new Part("Hvid skive", PartCategory.DIAL, "Test skive", 299.0, 5);
        testPart.setId(1L);
    }

    @Test
    void getAllParts_returnsList() {
        when(partRepository.findAll()).thenReturn(List.of(testPart));
        List<Part> result = partService.getAllParts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hvid skive");
    }

    @Test
    void getPartById_found_returnsPart() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        Part result = partService.getPartById(1L);
        assertThat(result.getName()).isEqualTo("Hvid skive");
    }

    @Test
    void getPartById_notFound_throwsException() {
        when(partRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> partService.getPartById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getPartsGroupedByCategory_groupsCorrectly() {
        Part strapPart = new Part("Lænke", PartCategory.STRAP, "Beskrivelse", 199.0, 3);
        when(partRepository.findAll()).thenReturn(List.of(testPart, strapPart));

        Map<PartCategory, List<Part>> grouped = partService.getPartsGroupedByCategory();

        assertThat(grouped).containsKey(PartCategory.DIAL);
        assertThat(grouped).containsKey(PartCategory.STRAP);
        assertThat(grouped.get(PartCategory.DIAL)).hasSize(1);
    }

    @Test
    void updateStock_negativeValue_throwsException() {
        assertThatThrownBy(() -> partService.updateStock(1L, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateStock_validValue_updatesStock() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(testPart));
        when(partRepository.save(any(Part.class))).thenReturn(testPart);

        Part updated = partService.updateStock(1L, 10);
        assertThat(updated.getStock()).isEqualTo(10);
    }

    @Test
    void getPartsGroupedByCategory_excludesOutOfStock() {
        Part outOfStock = new Part("Udsolgt del", PartCategory.CASE, "Ingen på lager", 500.0, 0);
        when(partRepository.findAll()).thenReturn(List.of(testPart, outOfStock));

        Map<PartCategory, List<Part>> grouped = partService.getPartsGroupedByCategory();

        assertThat(grouped).doesNotContainKey(PartCategory.CASE);
    }
}
