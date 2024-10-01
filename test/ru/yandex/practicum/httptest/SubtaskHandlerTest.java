package ru.yandex.practicum.httptest;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.handlers.SubtaskHandler;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskHandlerTest extends HandlersTest<SubtaskHandler> {

    private SubtaskHandlerTest() throws IOException {
    }

    // Получение
    @Test
    public void getSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtaskJson = gson.toJson(subtask).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());

        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/1"))
                .GET()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response4.statusCode());
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtaskJson = gson.toJson(subtask).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
    }

    // Добавление и обновление
    @Test
    public void addSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtaskJson = gson.toJson(subtask).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask1 = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask 3", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 30), 1);


        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtask1Json = gson.toJson(subtask1).replaceFirst("30", "PT30M");
        String subtask2Json = gson.toJson(subtask2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    // Удаление
    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtaskJson = gson.toJson(subtask).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertEquals(0, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask1 = new Subtask("Test 2", "Testing subtask 2", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 0), 1);
        Subtask subtask2 = new Subtask("Test 3", "Testing subtask 3", "NEW",
                30L, LocalDateTime.of(2024, 9, 25, 10, 30), 1);


        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];
        String subtask1Json = gson.toJson(subtask1).replaceFirst("30", "PT30M");
        String subtask2Json = gson.toJson(subtask2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode());

        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response4.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertEquals(0, subtasksFromManager.size(), "Некорректное количество задач");
    }
}
