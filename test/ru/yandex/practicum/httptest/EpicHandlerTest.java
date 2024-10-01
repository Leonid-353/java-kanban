package ru.yandex.practicum.httptest;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.handlers.EpicHandler;
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

public class EpicHandlerTest extends HandlersTest<EpicHandler> {

    private EpicHandlerTest() throws IOException {
    }

    // Получение
    @Test
    public void getEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/2"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1", "NEW");
        Epic epic2 = new Epic("Test 2", "Testing epic 2", "NEW");


        String[] json1 = gson.toJson(epic1).split(",");
        String epic1Json = json1[0] + "," + json1[5] + "," + json1[6] + "," + json1[7] + "," + json1[8];
        String[] json2 = gson.toJson(epic2).split(",");
        String epic2Json = json2[0] + "," + json2[5] + "," + json2[6] + "," + json2[7] + "," + json2[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epic2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
    }

    @Test
    public void getEpicSubasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");
        Subtask subtask = new Subtask("Test 2", "Testing subtask 2", "DONE",
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
                .uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getEpicSubtasks(1);

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    // Добавление и обновление
    @Test
    public void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1", "NEW");
        Epic epic2 = new Epic("Test 2", "Testing epic 2", "DONE");

        String[] json1 = gson.toJson(epic1).split(",");
        String epic1Json = json1[0] + "," + json1[5] + "," + json1[6] + "," + json1[7] + "," + json1[8];
        String[] json2 = gson.toJson(epic2).split(",");
        String epic2Json = json2[0] + "," + json2[5] + "," + json2[6] + "," + json2[7] + "," + json2[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .POST(HttpRequest.BodyPublishers.ofString(epic2Json))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    // Удаление
    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void deleteEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1", "NEW");

        String[] json = gson.toJson(epic).split(",");
        String epicJson = json[0] + "," + json[5] + "," + json[6] + "," + json[7] + "," + json[8];

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");
    }
}
