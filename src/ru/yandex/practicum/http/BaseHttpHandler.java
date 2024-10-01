package ru.yandex.practicum.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    // Чтение

    public String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public Optional<Integer> readId(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(path[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // Ответ

    public void sendText(HttpExchange exchange, String text) throws IOException {
        // для отправки общего ответа в случае успеха 200
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    public void sendTextPost(HttpExchange exchange, String text) throws IOException {
        // для отправки общего ответа в случае успеха 201
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange, String text) throws IOException {
        // для отправки ответа в случае, если объект не был найден 404
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    public void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        // для отправки ответа, если при создании или обновлении задача пересекается с уже существующими 406
        // или поля в теле запроса заполнены неверно
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    public void sendHasIllegal(HttpExchange exchange, String text) throws IOException {
        // для отправки ответа, если произошла ошибка записи 500
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(500, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    public void sendMethodNotAllowed(HttpExchange exchange, String text) throws IOException {
        // для отправки ответа, если запрашиваемого метода нет в обработчике 405
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(405, text.getBytes(StandardCharsets.UTF_8).length);
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }
}
