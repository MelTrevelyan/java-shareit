package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(false)
            .build();

    @SneakyThrows
    @Test
    public void getAllItemsByOwner_successful() {
        ItemViewDto itemViewDto = ItemViewDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(false)
                .comments(Collections.emptyList())
                .build();

        when(itemService.getAllItemsByOwner(1L, 0, 10)).thenReturn(List.of(itemViewDto));

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemViewDto)), result);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItemsByOwner(userId, -10, 20);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItemsByOwner(userId, 0, -20);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        List<ItemViewDto> items = List.of(ItemViewDto.builder().build());
        when(itemService.getAllItemsByOwner(userId, 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItemsByOwner(userId, 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @Test
    @SneakyThrows
    void findItemById_successful_thenReturnItemDto() {
        long userId = 1L;
        long itemId = 2L;
        ItemViewDto itemViewDto = ItemViewDto.builder().build();
        when(itemService.findItemById(itemId, userId)).thenReturn(itemViewDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemViewDto), result);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 0, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 0, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20))
                        .param("text", "item"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItemByText("item", -10, 20);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20))
                        .param("text", "item"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItemByText("item", 0, -20);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenWithParams_thenStatusOk() {
        long userId = 1L;
        String search = "item";
        List<ItemDto> items = List.of(itemDto);
        when(itemService.searchItemByText("item", 2, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(10))
                        .param("text", search))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByText("item", 2, 10);
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void createItem_whenNameIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = itemDto;
        newItemDto.setName(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenNameIsEmpty_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = itemDto;
        newItemDto.setName("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenDescriptionIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = itemDto;
        newItemDto.setDescription(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenDescriptionIsEmpty_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = itemDto;
        newItemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenAvailableIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = itemDto;
        newItemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_successful_thenReturnStatusOk() {
        long userId = 1L;
        when(itemService.create(itemDto, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentIsNull_thenReturnBadRequest() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto comment = CommentDto.builder().build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(comment, itemId, userId);
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentIsEmpty_thenReturnBadRequest() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto comment = CommentDto.builder()
                .text("")
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(comment, itemId, userId);
    }

    @SneakyThrows
    @Test
    void addComment_successful_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto comment = CommentDto.builder()
                .text(" ")
                .build();
        CommentDto commentOutDto = CommentDto.builder()
                .text("text")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(comment, itemId, userId)).thenReturn(commentOutDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentOutDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem_successful_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        when(itemService.update(itemDto, itemId, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }
}
