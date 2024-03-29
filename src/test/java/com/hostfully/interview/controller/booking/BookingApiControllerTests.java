package com.hostfully.interview.controller.booking;

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
class BookingApiControllerTests {

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
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_ValidDto_EntityIsReturnedAndInserted() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now(), LocalDate.now().plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("property.id", is(bookingCreateDto.getPropertyId())))
                .andExpect(jsonPath("startDate", is(bookingCreateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingCreateDto.getEndDate().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())))
                .andExpect(jsonPath("guests[0].name", is(bookingCreateDto.getGuests().get(0).getName())));
    }

    @Test
    public void createBooking_InvalidPropertyId_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("invalid-id", LocalDate.now(), LocalDate.now().plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property ID is required and must be a valid UUID")));
    }
    @Test
    public void createBooking_NonexistentProperty_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), LocalDate.now(), LocalDate.now().plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    public void createBooking_MissingPropertyId_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(null, LocalDate.now(), LocalDate.now().plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property ID is required and must be a valid UUID")));
    }

    @Test
    public void createBooking_MissingStartDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), null, LocalDate.now().plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Start date is required")));
    }

    @Test
    public void createBooking_MissingEndDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto(UUID.randomUUID().toString(), LocalDate.now(), null, guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("End date is required")));
    }
    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_StartDateAfterEndDate_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now().plusDays(1), LocalDate.now(), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Start date must be before end date")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_BookingOverlapping_BadRequestIsThrows() throws Exception {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

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

        var bookingCreateDto2 = new BookingCreateDto(propertyId, startDate.plusDays(1), endDate.plusDays(1), guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Dates are already booked")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_CancelBookingOverlapping_NewBookingIsReturn() throws Exception {
        var cancelBooking = createBooking();

        mockMvc.perform(put("/booking/{id}/action/cancel", cancelBooking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(cancelBooking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CANCELLED.toString())));

        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("property.id", is(bookingCreateDto.getPropertyId())))
                .andExpect(jsonPath("startDate", is(bookingCreateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingCreateDto.getEndDate().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())))
                .andExpect(jsonPath("guests[0].name", is(bookingCreateDto.getGuests().get(0).getName())));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_MissingGuests_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now(), LocalDate.now().plusDays(1), null);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Guests is required")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_EmptyGuests_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now(), LocalDate.now().plusDays(1), List.of());

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Guests is required")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_InvalidGuests_BadRequestIsThrows() throws Exception {
        var bookingCreateDto = new BookingCreateDto("555a2254-e8ff-4005-ada2-4d478b04a5d7", LocalDate.now(), LocalDate.now().plusDays(1), List.of(new GuestCreateDTO(null)));

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Guest name is required")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void createBooking_DatesBlocked_BadRequestIsThrows() throws Exception {
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);
        var blockCreateDto = new BlockCreateDto(startDate, endDate);

        mockMvc.perform(post("/property/{id}/block", propertyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Dates are already blocked")));
    }


    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void cancelBooking_ValidBooking_ReturnsBookingCancelled() throws Exception {
        var booking = createBooking();

        var result = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, result.getStatus());

        mockMvc.perform(put("/booking/{id}/action/cancel", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CANCELLED.toString())));

        var bookingCancel = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CANCELLED, bookingCancel.getStatus());
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void cancelBooking_BookingAlreadyCancelled_ReturnsBadRequest() throws Exception {
        var booking = createBooking();

        var result = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, result.getStatus());

        mockMvc.perform(put("/booking/{id}/action/cancel", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CANCELLED.toString())));

        var bookingCancel = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CANCELLED, bookingCancel.getStatus());

        mockMvc.perform(put("/booking/{id}/action/cancel", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Booking already cancelled")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void rebookBooking_ValidBooking_ReturnsBookingRebooked() throws Exception {
        var booking = createBooking();

        var result = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, result.getStatus());

        mockMvc.perform(put("/booking/{id}/action/cancel", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CANCELLED.toString())));

        var bookingCancel = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CANCELLED, bookingCancel.getStatus());

        mockMvc.perform(put("/booking/{id}/action/rebook", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())));

        var bookingRebook = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, bookingRebook.getStatus());
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void rebookBooking_BookingNotCancelled_ReturnsBadRequest() throws Exception {
        var booking = createBooking();

        var result = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, result.getStatus());

        mockMvc.perform(put("/booking/{id}/action/rebook", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Booking not cancelled")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void rebookBooking_DatesAlreadyBooked_ReturnsBadRequest() throws Exception {
        var booking = createBooking();

        mockMvc.perform(put("/booking/{id}/action/cancel", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CANCELLED.toString())));

        var newBooking = createBooking();

        mockMvc.perform(put("/booking/{id}/action/rebook", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Dates are already booked")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void getBooking_ValidBooking_ReturnsBooking() throws Exception {
        var booking = createBooking();

        mockMvc.perform(get("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void getBooking_InvalidBookingId_ReturnsBadRequest() throws Exception {
        var bookingId = UUID.randomUUID();

        mockMvc.perform(get("/booking/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void deleteBooking_ValidBooking_ReturnsNoContent() throws Exception {
        var booking = createBooking();

        mockMvc.perform(delete("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void deleteBooking_InvalidBookingId_ReturnsBadRequest() throws Exception {
        var bookingId = UUID.randomUUID();

        mockMvc.perform(delete("/booking/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void updateBooking_ValidBooking_ReturnsBookingUpdated() throws Exception {
        var booking = createBooking();

        var bookingUpdateDto = new BookingUpdateDto(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        mockMvc.perform(put("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())))
                .andExpect(jsonPath("startDate", is(bookingUpdateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingUpdateDto.getEndDate().toString())));

        var bookingUpdated = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, bookingUpdated.getStatus());
        Assertions.assertEquals(bookingUpdateDto.getStartDate(), bookingUpdated.getStartDate());
        Assertions.assertEquals(bookingUpdateDto.getEndDate(), bookingUpdated.getEndDate());
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void updateBooking_DatesInTheSameRange_ReturnsBookingUpdated() throws Exception {
        var booking = createBooking();
        var startDate = LocalDate.of(2023, 1, 10);
        var endDate = LocalDate.of(2023, 1, 15);

        var bookingUpdateDto = new BookingUpdateDto(startDate, endDate);

        mockMvc.perform(put("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(booking.getId().toString())))
                .andExpect(jsonPath("status", is(BookingStatus.CONFIRMED.toString())))
                .andExpect(jsonPath("startDate", is(bookingUpdateDto.getStartDate().toString())))
                .andExpect(jsonPath("endDate", is(bookingUpdateDto.getEndDate().toString())));

        var bookingUpdated = bookingRepository.findById(booking.getId()).get();
        Assertions.assertEquals(BookingStatus.CONFIRMED, bookingUpdated.getStatus());
        Assertions.assertEquals(bookingUpdateDto.getStartDate(), bookingUpdated.getStartDate());
        Assertions.assertEquals(bookingUpdateDto.getEndDate(), bookingUpdated.getEndDate());
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void updateBooking_InvalidBookingId_ReturnsBadRequest() throws Exception {
        var bookingId = UUID.randomUUID();
        var bookingUpdateDto = new BookingUpdateDto(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        mockMvc.perform(put("/booking/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void updateBooking_InvalidDates_ReturnsBadRequest() throws Exception {
        var booking = createBooking();

        var bookingUpdateDto = new BookingUpdateDto(LocalDate.now().plusDays(2), LocalDate.now().plusDays(1));

        mockMvc.perform(put("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Start date must be before end date")));
    }

    @Test
    @Sql(scripts = "/sql/insert-property.sql")
    public void updateBooking_BookingOverlapping_ReturnsBadRequest() throws Exception {
        createBooking();
        var startDate = LocalDate.of(2023, 2, 5);
        var endDate = LocalDate.of(2023, 2, 15);
        var booking = createBooking(startDate, endDate);
        var bookingUpdateDto = new BookingUpdateDto(startDate.minusMonths(1), endDate.minusMonths(1));

        mockMvc.perform(put("/booking/{id}", booking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Dates are already booked")));
    }

    private Booking createBooking() throws Exception {
        var startDate = LocalDate.of(2023, 1, 5);
        var endDate = LocalDate.of(2023, 1, 15);

        return createBooking(startDate, endDate);
    }

    private Booking createBooking(LocalDate startDate, LocalDate endDate) throws Exception {
        var propertyId = "555a2254-e8ff-4005-ada2-4d478b04a5d7";
        var bookingCreateDto = new BookingCreateDto(propertyId, startDate, endDate, guests);

        var responseBookingCreation = mockMvc.perform(post("/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseBookingCreation, Booking.class);
    }
}
