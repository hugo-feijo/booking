package com.hostfully.interview.controller.guest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.model.entity.Guest;
import com.hostfully.interview.repository.BookingRepository;
import com.hostfully.interview.repository.PropertyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class GuestApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void setUp() {
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createGuest_ValidRequest_ShouldReturnBooking() throws Exception {
        var booking = createBooking();
        var guest = new GuestCreateDTO("guest2");

        var result = mockMvc.perform(post("/booking/{id}/guest", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var bookingUpdated = objectMapper.readValue(result, Booking.class);

        assertEquals(2, bookingUpdated.getGuests().size());
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createGuest_InvalidBookingId_ShouldReturnBadRequest() throws Exception {
        var guest = new GuestCreateDTO("guest2");

        mockMvc.perform(post("/booking/{id}/guest", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createGuest_InvalidGuestName_ShouldReturnBadRequest() throws Exception {
        var booking = createBooking();
        var guest = new GuestCreateDTO(null);

        mockMvc.perform(post("/booking/{id}/guest", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message", is("Guest name is required")));
    }

    public Booking createBooking() {
        var property = propertyRepository.findById(UUID.fromString("555a2254-e8ff-4005-ada2-4d478b04a5d7")).get();
        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusDays(1);
        var guest = new Guest(null, "guest1", LocalDate.now(), null);
        var booking = new Booking(null, property, startDate, endDate, BookingStatus.CONFIRMED, LocalDate.now(), null, List.of(guest));
        return bookingRepository.save(booking);
    }
}
