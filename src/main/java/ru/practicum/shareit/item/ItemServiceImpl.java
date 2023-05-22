package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.NotOwnerForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemViewDto> getAllItemsByOwner(Long id) {
        Map<Long, Item> itemMap = repository.findAllByOwnerId(id)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, List<Booking>> bookingMap = bookingRepository.findByItemIdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        return itemMap.values()
                .stream()
                .map(item -> ItemMapper.toItemViewForOwnerDto(item, bookingMap.getOrDefault(item.getId(),
                                Collections.emptyList())
                        .stream()
                        .map(BookingMapper::toBookingViewDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long id) {
        UserDto userDto = userService.findUserById(id);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        Item itemToUpdate = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item was not found"));
        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NotOwnerForbiddenException("User is not the owner of an item");
        }

        boolean updated = false;
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
            updated = true;
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
            updated = true;
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
            updated = true;
        }
        if (updated) {
            return ItemMapper.toItemDto(repository.save(itemToUpdate));
        }
        log.warn("update of item with id {} failed", itemId);
        throw new ItemValidationException("Unable to update empty parameters of item");
    }

    @Override
    public ItemViewDto findItemById(Long itemId, Long userId) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item was not found"));
        if (userId.equals(item.getOwner().getId())) {
            return ItemMapper.toItemViewForOwnerDto(item,
                    bookingRepository.findByItemId(itemId)
                            .stream()
                            .map(BookingMapper::toBookingViewDto)
                            .collect(Collectors.toList()));
        }
        return ItemMapper.toItemViewForBookerDto(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.mapToItemDto(repository.searchItemByText(text)
                .stream().filter(Item::getAvailable).collect(Collectors.toList()));
    }
}
