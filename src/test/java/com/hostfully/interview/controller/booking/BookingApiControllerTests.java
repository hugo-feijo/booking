package com.hostfully.interview.controller.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.BookingStatus;
import com.hostfully.interview.model.entity.Property;
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
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookingApiControllerTests {

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

    //TODO: test blocking scenario


    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createProperty_ValidDto_EntityIsReturnedAndInserted() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now(), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("property.id", is(bookingCreateDto.getPropertyId())))
                .andExpect(jsonPath("startDate", is(bookingCreateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingCreateDto.getEndDate().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())));
    }

    @Test
    public void createProperty_InvalidPropertyId_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("invalid-id", LocalDate.now(), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property ID is required and must be a valid UUID")));
    }
    @Test
    public void createProperty_NonexistentProperty_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), LocalDate.now(), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    public void createProperty_MissingPropertyId_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(null, LocalDate.now(), LocalDate.now().plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property ID is required and must be a valid UUID")));
    }

    @Test
    public void createProperty_MissingStartDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), null, LocalDate.now().plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Start date is required")));
    }

    @Test
    public void createProperty_MissingEndDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), LocalDate.now(), null);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("End date is required")));
    }
    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createProperty_StartDateAfterEndDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now().plusDays(1), LocalDate.now());

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Start date must be before end date")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createProperty_BookingOverlapping_BadRequestIsThrows() throws Exception {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("property.id", is(bookingCreateDto.getPropertyId())))
                .andExpect(jsonPath("startDate", is(bookingCreateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingCreateDto.getEndDate().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())));

        var bookingCreateDto2 = new BookingCreateDto(propertyId, startDate.plusDays(1), endDate.plusDays(1));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Dates already booked")));
    }
}
