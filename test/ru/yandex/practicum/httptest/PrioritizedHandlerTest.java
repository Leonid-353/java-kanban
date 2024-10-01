package ru.yandex.practicum.httptest;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.handlers.PrioritizedHandler;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrioritizedHandlerTest extends HandlersTest<PrioritizedHandler> {

    private PrioritizedHandlerTest() throws IOException {
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {

        Task task1 = new Task("Test 1", "Testing task 1", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0));
        Task task2 = new Task("Test 2", "Testing task 2", "IN_PROGRESS",
                30L, LocalDateTime.of(2024, 9, 25, 9, 30));

        String task1Json = gson.toJson(task1).replaceFirst("30", "PT30M");
        String task2Json = gson.toJson(task2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestPost1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();
        HttpRequest requestPost2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> responsePost1 = client.send(requestPost1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePost1.statusCode());
        HttpResponse<String> responsePost2 = client.send(requestPost2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responsePost2.statusCode());

        HttpRequest requestGet1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();
        HttpRequest requestGet2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .GET()
                .build();

        HttpResponse<String> responseGet1 = client.send(requestGet1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet1.statusCode());
        HttpResponse<String> responseGet2 = client.send(requestGet2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGet2.statusCode());

        HttpRequest requestGetPrioritized = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> responseGetPrioritized = client.send(requestGetPrioritized, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetPrioritized.statusCode());

        List<Task> prioritizedFromManager = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedFromManager, "Задачи не возвращаются");
        assertEquals(2, prioritizedFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", prioritizedFromManager.getFirst().getName(), "Некорректное имя задачи");
    }
}
