package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingOutDto create(BookingDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userService.findUserById(userId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId()));
        if (!item.getAvailable()) {
            throw new BookingValidationException("Unable to create booking with an unavailable item");
        }
        item.setOwner(user);
        Booking booking = BookingMapper.toBooking(bookingDto);
        checkCorrectTiming(booking);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto setBookingApproval(Long userId, Boolean approved, Long bookingId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking was not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotOwnerForbiddenException("Only the owner of an item is allowed to set the booking approval");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingOutDto findBookingById(Long bookingId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking was not found"));
        if (userId.equals(booking.getBooker().getId())
                || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        }
        throw new UserAccessForbiddenException("Only the owner of an item or the booker are allowed to see " +
                "booking information");
    }

    @Override
    public List<BookingOutDto> findBookingsOfUser(BookingState state, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = repository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case FUTURE:
                bookings = repository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            default:
                throw new UnsupportedBookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    private void checkCorrectTiming(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        if (booking.getEnd().isBefore(now)) {
            throw new BookingValidationException("Unable to create booking with end time in the past");
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingValidationException("Unable to create booking with end time before start time");
        } else if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BookingValidationException("Unable to create booking with end time equal to start time");
        } else if (booking.getStart().isBefore(now)) {
            throw new BookingValidationException("Unable to create booking with start time in the past");
        }
    }
}
