package com.hostfully.interview.repository;

import com.hostfully.interview.model.entity.Block;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<Block, UUID> {

    @Query(value =
    """
        SELECT exists(
            SELECT PROPERTY_ID FROM BLOCK
            WHERE PROPERTY_ID = ?1
            AND (ID != ?2 OR ?2 IS NULL)
            AND START_DATE <= ?4
            AND END_DATE >= ?3)
    """, nativeQuery = true)
    boolean existByPropertyIdAndDateRange(String propertyId, String blockId, LocalDate startDate, LocalDate endDate);

    List<Block> findAllByProperty(Property property);
}
