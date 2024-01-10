package com.hostfully.interview.repository;

import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query(value =
    """
        SELECT exists(
            SELECT PROPERTY_ID FROM BOOKING
            WHERE PROPERTY_ID = ?1
            AND START_DATE <= ?3
            AND END_DATE >= ?2)
    """, nativeQuery = true)
    boolean existByPropertyIdAndDateRange(String propertyId, LocalDate startDate, LocalDate endDate);

    List<Booking> findAllByProperty(Property property);
}
