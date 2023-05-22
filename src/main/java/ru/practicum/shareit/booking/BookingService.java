package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingViewDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {

    BookingOutDto create(BookingDto bookingDto, Long userId);

    BookingOutDto setBookingApproval(Long userId, Boolean approved, Long bookingId);

    BookingOutDto findBookingById(Long bookingId, Long userId);

    List<BookingOutDto> findBookingsOfUser(BookingState state, Long userId);

    List<BookingOutDto> findBookingsOfOwner(BookingState state, Long userId);

    List<BookingViewDto> findByItemId(Long itemId);
}
