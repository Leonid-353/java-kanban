package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.manager.TasksManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TasksManager taskManager;

    public PrioritizedHandler(Gson gson, TasksManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED -> {
                try {
                    sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case UNKNOWN -> sendMethodNotAllowed(exchange, "Такого эндпоинта не существует");

        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path.length == 2 && path[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
