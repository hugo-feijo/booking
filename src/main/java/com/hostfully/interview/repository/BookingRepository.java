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
            AND (ID != ?2 OR ?2 IS NULL)
            AND STATUS != 'CANCELLED'
            AND START_DATE <= ?4
            AND END_DATE >= ?3)
    """, nativeQuery = true)
    boolean existByPropertyIdAndDateRange(String propertyId, String bookingId, LocalDate startDate, LocalDate endDate);

    List<Booking> findAllByProperty(Property property);
}
