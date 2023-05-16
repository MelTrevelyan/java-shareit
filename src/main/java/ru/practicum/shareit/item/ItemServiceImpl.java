package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemValidationException;
import ru.practicum.shareit.exception.NotOwnerForbiddenException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long id) {
        UserDto userDto = userService.findUserById(id);
        return ItemMapper.mapToItemDto(repository.findAllByOwnerId(id));
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
    public ItemDto findItemById(Long id) {
        return ItemMapper.toItemDto(repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item was not found")));
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
