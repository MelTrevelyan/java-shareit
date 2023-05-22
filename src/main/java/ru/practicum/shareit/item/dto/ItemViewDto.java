package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingViewDto;

@Data
@Builder
public class ItemViewDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingViewDto lastBooking;
    private BookingViewDto nextBooking;
}
