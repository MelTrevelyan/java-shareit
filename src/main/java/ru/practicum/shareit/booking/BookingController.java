package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto create(@RequestBody @Valid BookingDto bookingDto,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutDto setBookingApproval(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam @NotNull Boolean approved,
                                         @PathVariable Long bookingId) {
        return bookingService.setBookingApproval(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto findBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> findBookingsOfUser(@RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.valueOf(state);
        return bookingService.findBookingsOfUser(bookingState, userId);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findBookingsOfOwner(@RequestParam(defaultValue = "ALL", required = false) String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingState bookingState = BookingState.valueOf(state);
        return bookingService.findBookingsOfOwner(bookingState, userId);
    }
}
