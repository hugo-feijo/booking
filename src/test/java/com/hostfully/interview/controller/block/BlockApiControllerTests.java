package com.hostfully.interview.controller.block;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.interview.model.dto.BlockCreateDto;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.BookingUpdateDto;
import com.hostfully.interview.model.dto.GuestCreateDTO;
import com.hostfully.interview.model.entity.Booking;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.repository.BlockRepository;
import com.hostfully.interview.repository.BookingRepository;
import com.hostfully.interview.repository.PropertyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BlockApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<GuestCreateDTO> guests = List.of(new GuestCreateDTO("John"));

    @AfterEach
    void setUp() {
        blockRepository.deleteAll();
        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createBlock_ValidPropertyIdAndDates_ReturnsCreatedBlock() throws Exception {
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var blockCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/property/{propertyId}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("property.id", is(propertyId)))
                .andExpect(jsonPath("startDate", is(startDate.toString())))
                .andExpect(jsonPath("endDate", is(endDate.toString())));
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createBlock_InvalidPropertyId_ReturnsBadRequest() throws Exception {
        var propertyId = "invalid-property-id";
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var blockCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/property/{propertyId}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createBlock_DatesAlreadyBlocked_ReturnsBadRequest() throws Exception {
        createBlock();

        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var startDate = LocalDate.of(2023, 1, 10);
        var endDate = LocalDate.of(2023, 1, 25);
        var blockCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/property/{propertyId}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message", is("Dates already blocked")));
    }

    @Test
    @Sql("classpath:sql/insert-property.sql")
    public void createBlock_DatesAlreadyBooked_ReturnsBadRequest() throws Exception {
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var startDate = LocalDate.of(2023, 1, 10);
        var endDate = LocalDate.of(2023, 1, 25);
        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/booking", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var blockCreateDto = new BlockCreateDto(startDate, endDate);

        mockMvc.perform(post("/property/{propertyId}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("message", is("Dates already booked")));
    }


    private Booking createBlock() throws Exception {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);

        return createBlock(startDate, endDate);
    }

    private Booking createBlock(LocalDate startDate, LocalDate endDate) throws Exception {
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var blockCreateDto = new BlockCreateDto(startDate, endDate);

        var responseBookingCreation = mockMvc.perform(post("/property/{id}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseBookingCreation, Booking.class);
    }
}
