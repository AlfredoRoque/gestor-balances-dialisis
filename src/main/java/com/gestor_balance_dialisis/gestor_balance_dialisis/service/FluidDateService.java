package com.gestor_balance_dialisis.gestor_balance_dialisis.service;

import com.gestor_balance_dialisis.gestor_balance_dialisis.entity.FluidDate;
import com.gestor_balance_dialisis.gestor_balance_dialisis.enums.StatusEnum;
import com.gestor_balance_dialisis.gestor_balance_dialisis.repository.FluidDateRepository;
import com.gestor_balance_dialisis.gestor_balance_dialisis.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing fluid date records, including retrieving active fluid dates from the database.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FluidDateService {

    private final FluidDateRepository fluidDateRepository;

    /**
     * Retrieves all active fluid dates available in the system for fluid balance records.
     *
     * @return a list of active fluid dates as strings
     */
    public List<String> getAllFluidDates() {
        List<String> listDates = new ArrayList<>();
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH:mm");
        fluidDateRepository.getFluidDateByStatus(StatusEnum.ACTIVO)
                .forEach(fluidDate -> listDates.add(fluidDate.getDate().atZone(SecurityUtils.getUserZone())
                        .format(formatterHour)));
        return listDates;
    }
}
