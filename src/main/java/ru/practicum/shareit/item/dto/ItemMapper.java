package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForOwnerDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.*;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemDto> mapToItemDto(Collection<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemForOwnerDto toItemForOwnerDto(Item item, List<BookingForOwnerDto> bookings) {
        LocalDateTime now = LocalDateTime.now();

        return ItemForOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(bookings.stream().filter(b -> b.getStart().isBefore(now))
                        .max(Comparator.comparing(BookingForOwnerDto::getStart)).orElse(null))
                .nextBooking(bookings.stream().filter(b -> b.getStart().isAfter(now))
                        .min(Comparator.comparing(BookingForOwnerDto::getStart)).orElse(null))
                .build();
    }
}
