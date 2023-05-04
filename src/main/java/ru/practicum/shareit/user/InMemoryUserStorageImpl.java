package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class InMemoryUserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        setNextId(user);
        users.put(user.getId(), user);
        log.info("added a new user with id {}", user.getId());
        return user;
    }

    @Override
    public User update(User user, Long id) {
        users.put(id, user);
        log.info("user with id {} was updated", id);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new UserNotFoundException("User was not found"));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("user with id {} was deleted", id);
    }

    @Override
    public boolean doesIdExist(Long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean doesEmailNotExist(String email) {
        return !users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private void setNextId(User user) {
        user.setId(nextId.incrementAndGet());
    }
}
