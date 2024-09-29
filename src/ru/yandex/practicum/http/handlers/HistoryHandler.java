package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.manager.TasksManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TasksManager taskManager;

    public HistoryHandler(Gson gson, TasksManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY -> {
                try {
                    sendText(exchange, gson.toJson(taskManager.getHistory()));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case UNKNOWN -> sendHasIllegal(exchange, "Такого эндпоинта не существует");

        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path.length == 2 && path[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
