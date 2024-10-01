package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TasksManager taskManager;

    public EpicHandler(Gson gson, TasksManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC -> {
                try {
                    Optional<Integer> epicIdOpt = readId(exchange);
                    if (epicIdOpt.isEmpty() || !taskManager.isContainedInEpics(epicIdOpt.get())) {
                        sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    String response = gson.toJson(taskManager.getEpic(epicIdOpt.get()));
                    sendText(exchange, response);
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case GET_EPICS -> {
                try {
                    sendText(exchange, gson.toJson(taskManager.getEpics()));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case GET_EPICSUBTASKS -> {
                try {
                    Optional<Integer> epicIdOpt = readId(exchange);
                    if (epicIdOpt.isEmpty() || !taskManager.isContainedInEpics(epicIdOpt.get())) {
                        sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    sendText(exchange, gson.toJson(taskManager.getEpicSubtasks(epicIdOpt.get())));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case POST_ADDEPIC -> {
                try {
                    JsonElement jsonElement = gson.fromJson(readText(exchange), JsonElement.class);
                    if (!jsonElement.isJsonObject()) {
                        sendHasIllegal(exchange, "Некорректное тело запроса.");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (!jsonObject.has("status")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"status\".");
                    }
                    String containsElement = jsonElement.toString();
                    if ((containsElement.contains("name") &&
                            containsElement.contains("description") &&
                            containsElement.contains("startTime") &&
                            containsElement.contains("duration")) ||
                            (containsElement.contains("name") &&
                                    containsElement.contains("description"))
                    ) {
                        Epic epic = gson.fromJson(jsonObject, Epic.class);
                        epic.setTaskType(TaskType.EPIC);
                        epic.setSubtaskId(new ArrayList<>());
                        taskManager.addNewEpic(epic);
                        sendTextPost(exchange, "Задача успешно добавлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case POST_UPDATEEPIC -> {
                try {
                    JsonElement jsonElement = gson.fromJson(readText(exchange), JsonElement.class);
                    if (!jsonElement.isJsonObject()) {
                        sendHasIllegal(exchange, "Некорректное тело запроса.");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (!jsonObject.has("status")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"status\".");
                    }
                    String containsElement = jsonElement.toString();
                    if ((containsElement.contains("name") &&
                            containsElement.contains("description") &&
                            containsElement.contains("startTime") &&
                            containsElement.contains("duration")) ||
                            (containsElement.contains("name") &&
                                    containsElement.contains("description"))
                    ) {
                        Optional<Integer> epicIdOpt = readId(exchange);
                        if (epicIdOpt.isEmpty() || !taskManager.isContainedInEpics(epicIdOpt.get())) {
                            sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                            return;
                        }
                        Epic epic = gson.fromJson(jsonObject, Epic.class);
                        epic.setTaskType(TaskType.EPIC);
                        epic.setId(epicIdOpt.get());
                        epic.setSubtaskId(new ArrayList<>());
                        taskManager.updateEpic(epic);
                        sendTextPost(exchange, "Задача успешно обновлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case DELETE_EPIC -> {
                try {
                    Optional<Integer> epicIdOpt = readId(exchange);
                    if (epicIdOpt.isEmpty() || !taskManager.isContainedInEpics(epicIdOpt.get())) {
                        sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    taskManager.deleteEpic(epicIdOpt.get());
                    sendText(exchange, "Задача с ID: " + epicIdOpt.get() + " успешно удалена.");
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case DELETE_EPICS -> {
                taskManager.deleteEpics();
                sendText(exchange, "Все задачи удалены.");
            }
            case UNKNOWN -> sendMethodNotAllowed(exchange, "Такого эндпоинта не существует");

        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path.length == 2 && path[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_ADDEPIC;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPICS;
            }
        } else if (path.length == 3 && path[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATEEPIC;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        } else if (path.length == 4 && path[1].equals("epics")) {
            return Endpoint.GET_EPICSUBTASKS;
        }
        return Endpoint.UNKNOWN;
    }
}
