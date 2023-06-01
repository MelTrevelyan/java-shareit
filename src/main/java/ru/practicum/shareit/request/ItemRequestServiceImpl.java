package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;

    @Override
    public ItemRequest findRequestById(Long itemRequestId) {
        return repository.findById(itemRequestId).orElseThrow(() -> new ItemNotFoundException("Item was not found"));
    }
}
