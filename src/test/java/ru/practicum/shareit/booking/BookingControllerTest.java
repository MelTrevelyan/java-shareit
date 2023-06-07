package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private BookingOutDto bookingOutDto;
    private BookingDto bookingDto;

    @BeforeEach
    public void createBookings() {
        User user = new User(2L, "user", "user@mail.ru");
        Item item = new Item(1L, "item", "description", true, null, null);
        bookingDto = new BookingDto(1L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), null);
        bookingOutDto = new BookingOutDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1), BookingStatus.WAITING, user, item);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUser_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        List<BookingOutDto> bookings = List.of(bookingOutDto);
        when(bookingService.findBookingsOfUser(BookingState.ALL, userId, 0, 10))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingsOfUser(BookingState.ALL, userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUser_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfUser(BookingState.ALL, userId, -10, 20);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUser_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfUser(BookingState.ALL, userId, 10, -20);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUser_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfUser(BookingState.ALL, userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void findBookingsOfUser_whenWithParams_thenStatusOkAndReturnCollection() {
        long userId = 1L;
        List<BookingOutDto> bookings = List.of(bookingOutDto);
        when(bookingService.findBookingsOfUser(BookingState.WAITING, userId, 0, 10))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingsOfUser(BookingState.WAITING, userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        List<BookingOutDto> bookings = List.of(bookingOutDto);
        when(bookingService.findBookingsOfOwner(BookingState.ALL, userId, 0, 10))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingsOfOwner(BookingState.ALL, userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfOwner(BookingState.ALL, userId, -10, 10);
    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfOwner(BookingState.ALL, userId, 0, -10);
    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsOfOwner(BookingState.ALL, userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void findBookingsOfOwner_whenWithParams_thenStatusOkAndReturnCollection() {
        long userId = 1L;
        List<BookingOutDto> bookings = List.of(bookingOutDto);
        when(bookingService.findBookingsOfOwner(BookingState.WAITING, userId, 0, 10))
                .thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "WAITING")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).findBookingsOfOwner(BookingState.WAITING, userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    void findBookingById_whenInvoke_thenReturnBookingDtoFullOut() {
        long userId = 1L;
        long bookingId = 2L;
        when(bookingService.findBookingById(bookingId, userId)).thenReturn(bookingOutDto);

        String result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutDto), result);
    }

    @SneakyThrows
    @Test
    void createBooking_whenStartDateIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDto.setStart(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(bookingDto, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenEndDateIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDto.setEnd(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(bookingDto, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenItemIdIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(bookingDto, userId);
    }

    @SneakyThrows
    @Test
    public void createBooking_successful_thenReturnStatusIsOk() {
        long userId = 1;
        when(bookingService.create(bookingDto, userId)).thenReturn(bookingOutDto);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutDto), result);
    }

    @SneakyThrows
    @Test
    void setBookingApproval_whenWithoutParam_thenReturnStatusIs500() {
        long userId = 1L;
        long bookingId = 2L;
        when(bookingService.setBookingApproval(userId, true, bookingId)).thenReturn(bookingOutDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).setBookingApproval(userId, true, bookingId);
    }

    @SneakyThrows
    @Test
    void setBookingApproval_whenWithParam_thenReturnStatusIsOk() {
        long userId = 1L;
        long bookingId = 2L;
        when(bookingService.setBookingApproval(userId, true, bookingId)).thenReturn(bookingOutDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutDto), result);
    }
}
