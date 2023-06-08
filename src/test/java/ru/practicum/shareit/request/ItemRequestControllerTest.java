package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService service;

    private ItemRequestOutDto outDto;

    @BeforeEach
    public void createRequest() {
        User user = new User(1L, "user", "user@mail.ru");
        outDto = new ItemRequestOutDto(1L, "description", user, LocalDateTime.now(), new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void getUserRequestsWithAnswers_successful_thenReturnListOfRequest() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);
        when(service.getUserRequestsWithAnswers(userId)).thenReturn(requests);

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getUserRequestsWithAnswers(userId);
        assertEquals(objectMapper.writeValueAsString(requests), result);
    }

    @SneakyThrows
    @Test
    void getItemRequest_successful_thenReturnRequest() {
        long userId = 1L;
        long requestId = 2L;
        when(service.getItemRequest(userId, requestId)).thenReturn(outDto);

        String result = mvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getItemRequest(userId, requestId);
        assertEquals(objectMapper.writeValueAsString(outDto), result);
    }

    @SneakyThrows
    @Test
    void getRequestsOfOthers_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);
        when(service.getRequestsOfOthers(userId, 0, 10)).thenReturn(requests);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getRequestsOfOthers(userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(requests), result);
    }

    @SneakyThrows
    @Test
    void getRequestsOfOthers_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);
        when(service.getRequestsOfOthers(userId, 1, 10)).thenReturn(requests);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getRequestsOfOthers(userId, 1, 10);
        assertEquals(objectMapper.writeValueAsString(requests), result);
    }

    @SneakyThrows
    @Test
    void getRequestsOfOthers_whenWithParams_thenStatusOk() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);
        when(service.getRequestsOfOthers(userId, 2, 20)).thenReturn(requests);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getRequestsOfOthers(userId, 2, 20);
        assertEquals(objectMapper.writeValueAsString(requests), result);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenParamFromLessZero_thenStatusBadRequest() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);
        when(service.getRequestsOfOthers(userId, -10, 20)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getRequestsOfOthers(userId, -10, 20);
    }

    @SneakyThrows
    @Test
    void getRequestsOfOthers_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getRequestsOfOthers(userId, 0, -20);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;
        List<ItemRequestOutDto> requests = List.of(outDto);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getRequestsOfOthers(userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemRequestInDto itemRequestDtoIn = new ItemRequestInDto(null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(itemRequestDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestIsBlank_thenReturnBadRequest() {
        long userId = 1L;
        ItemRequestInDto itemRequestDtoIn = new ItemRequestInDto("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(itemRequestDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void createItemRequest_successful_thenReturnStatusOk() {
        long userId = 1L;
        ItemRequestInDto itemRequestDtoIn = new ItemRequestInDto("desc");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isOk());

        verify(service).create(itemRequestDtoIn, userId);
    }
}
