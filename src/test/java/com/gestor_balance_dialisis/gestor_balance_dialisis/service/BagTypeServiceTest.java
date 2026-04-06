package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.BagType;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.BagTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for BagTypeService, covering the save and getAllBagTypes methods.
 * Uses Mockito to mock the BagTypeRepository and verify interactions.
 * Tests ensure that the service correctly maps between entities and DTOs, and that repository methods are called as expected.
 */
@ExtendWith(MockitoExtension.class)
class BagTypeServiceTest {

    @Mock private BagTypeRepository bagTypeRepository;

    @InjectMocks private BagTypeService bagTypeService;

    /**
     * Helper method to create a sample BagType entity for testing purposes.
     * @return a BagType instance with preset id, type, and description values.
     */
    private BagType buildBagType() {
        BagType bt = new BagType();
        bt.setId(1L);
        bt.setType("1.5");
        bt.setDescription("Standard bag 1.5L");
        return bt;
    }

    // ─── save ────────────────────────────────────────────────────────────────

    /**
     * Test that saving a bag type persists the entity and returns a correctly mapped response.
     * Verifies that the returned DTO contains the expected id, type, and description,
     * and that the repository save method is invoked once.
     */
    @Test
    void save_returnsSavedBagType() {
        BagTypeRequest req = new BagTypeRequest();
        req.setType("1.5");
        req.setDescription("Standard bag");

        BagType saved = buildBagType();
        when(bagTypeRepository.save(any(BagType.class))).thenReturn(saved);

        BagTypeResponse result = bagTypeService.save(req);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo("1.5");
        assertThat(result.getDescription()).isEqualTo("Standard bag 1.5L");
        verify(bagTypeRepository).save(any(BagType.class));
    }

    /**
     * Test that saving a bag type with different field values correctly persists and returns those values.
     * Verifies that the response DTO reflects the type and description provided in the request.
     */
    @Test
    void save_persistsCorrectFields() {
        BagTypeRequest req = new BagTypeRequest();
        req.setType("2.0");
        req.setDescription("Large bag");

        BagType saved = new BagType();
        saved.setId(2L);
        saved.setType("2.0");
        saved.setDescription("Large bag");
        when(bagTypeRepository.save(any(BagType.class))).thenReturn(saved);

        BagTypeResponse result = bagTypeService.save(req);

        assertThat(result.getType()).isEqualTo("2.0");
        assertThat(result.getDescription()).isEqualTo("Large bag");
    }

    // ─── getAllBagTypes ───────────────────────────────────────────────────────

    /**
     * Test that getAllBagTypes returns a correctly mapped list of bag type responses.
     * Verifies that all entities from the repository are converted to DTOs with the correct type values.
     */
    @Test
    void getAllBagTypes_returnsMappedList() {
        BagType bt1 = buildBagType();
        BagType bt2 = new BagType(2L, "2.5", "Large 2.5L");
        when(bagTypeRepository.findAll()).thenReturn(List.of(bt1, bt2));

        List<BagTypeResponse> result = bagTypeService.getAllBagTypes();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo("1.5");
        assertThat(result.get(1).getType()).isEqualTo("2.5");
    }

    /**
     * Test that getAllBagTypes returns an empty list when no bag types exist in the repository.
     * Verifies that the service correctly handles the empty result from the repository.
     */
    @Test
    void getAllBagTypes_empty_returnsEmptyList() {
        when(bagTypeRepository.findAll()).thenReturn(List.of());

        List<BagTypeResponse> result = bagTypeService.getAllBagTypes();

        assertThat(result).isEmpty();
    }
}

