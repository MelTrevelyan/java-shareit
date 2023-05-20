package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;

@Data
@Builder
public class ItemForOwnerDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForOwnerDto lastBooking;
    private BookingForOwnerDto nextBooking;
}
