package ru.yandex.practicum.httptest;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.handlers.TaskHandler;
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

public class TaskHandlerTest extends HandlersTest<TaskHandler> {

    private TaskHandlerTest() throws IOException {
    }

    // Получение
    @Test
    public void getTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));

        String taskJson = gson.toJson(task).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));
        Task task2 = new Task("Test 2", "Testing task 2",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 30));

        String task1Json = gson.toJson(task1).replaceFirst("30", "PT30M");
        String task2Json = gson.toJson(task2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
    }

    // Добавление и обновление
    @Test
    public void addTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));
        Task task2 = new Task("Test 2", "Testing task 2",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 15));

        String task1Json = gson.toJson(task1).replaceFirst("30", "PT30M");
        String task2Json = gson.toJson(task2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));
        Task task2 = new Task("Test 2", "Testing task 2",
                "DONE", 30L, LocalDateTime.of(2024, 9, 25, 11, 0));

        String task1Json = gson.toJson(task1).replaceFirst("30", "PT30M");
        String task2Json = gson.toJson(task2).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    // Удаление
    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));

        String taskJson = gson.toJson(task).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                "NEW", 30L, LocalDateTime.of(2024, 9, 25, 10, 0));

        String taskJson = gson.toJson(task).replaceFirst("30", "PT30M");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }
}
