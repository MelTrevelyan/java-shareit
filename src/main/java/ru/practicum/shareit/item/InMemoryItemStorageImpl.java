package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    @Override
    public List<Item> getAllItemsByOwner(Long id) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        setNextId(item);
        items.put(item.getId(), item);
        log.info("added a new item with id {}", item.getId());
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("item with id {} was updated", item.getId());
        return item;
    }

    @Override
    public Item findItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new ItemNotFoundException("Item was not found");
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
        log.info("item with id {} was deleted", id);
    }

    @Override
    public List<Item> searchItemByText(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private void setNextId(Item item) {
        item.setId(nextId.incrementAndGet());
    }
}
