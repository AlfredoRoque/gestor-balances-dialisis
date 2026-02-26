package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeRequest;
import com.gestor_balance_dialisis.gestor_balance_dialisis.dto.BagTypeResponse;
import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.BagType;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.BagTypeRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.Utility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing bag types, including saving new bag types and retrieving existing ones.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BagTypeService {

    private final BagTypeRepository bagTypeRepository;

    /**
     * Saves a new bag type based on the provided request data.
     *
     * @param bagTypeRequest the request containing the bag type information to be saved
     * @return a response containing the saved bag type information
     */
    @Transactional
    public BagTypeResponse save(BagTypeRequest bagTypeRequest) {
        log.info("param : {}",bagTypeRequest.getType());
        return new BagTypeResponse(bagTypeRepository.save(new BagType(bagTypeRequest)));
    }

    /**
     * Retrieves all bag types from the database.
     *
     * @return a list of responses containing the information of all bag types
     */
    public List<BagTypeResponse> getAllBagTypes() {
        return bagTypeRepository.findAll()
                .stream().map(BagTypeResponse::new).toList();
    }
}
