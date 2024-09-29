package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.TaskType;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TasksManager taskManager;

    public SubtaskHandler(Gson gson, TasksManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASK -> {
                try {
                    Optional<Integer> subtaskIdOpt = readId(exchange);
                    if (subtaskIdOpt.isEmpty() || !taskManager.isContainedInSubtasks(subtaskIdOpt.get())) {
                        sendNotFound(exchange, "Подзадача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    String response = gson.toJson(taskManager.getSubtask(subtaskIdOpt.get()));
                    sendText(exchange, response);
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case GET_SUBTASKS -> {
                try {
                    sendText(exchange, gson.toJson(taskManager.getSubtasks()));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case POST_ADDSUBTASK -> {
                try {
                    JsonElement jsonElement = gson.fromJson(readText(exchange), JsonElement.class);
                    if (!jsonElement.isJsonObject()) {
                        sendHasIllegal(exchange, "Некорректное тело запроса.");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (!jsonObject.has("status")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"status\".");
                    }
                    if (!jsonObject.has("epicId")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"epicId\".");
                    }
                    if (!taskManager.isContainedInEpics(jsonObject.get("epicId").getAsInt())) {
                        sendNotFound(exchange, "Эпик с ID: " +
                                jsonObject.get("epicId").getAsInt() + " не найден.");
                    }
                    String containsElement = jsonElement.toString();
                    if ((containsElement.contains("name") &&
                            containsElement.contains("description") &&
                            containsElement.contains("startTime") &&
                            containsElement.contains("duration")) ||
                            (containsElement.contains("name") &&
                                    containsElement.contains("description"))
                    ) {
                        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
                        subtask.setTaskType(TaskType.SUBTASK);
                        taskManager.addNewSubtask(subtask);
                        sendTextPost(exchange, "Подзадача успешно добавлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case POST_UPDATESUBTASK -> {
                try {
                    JsonElement jsonElement = gson.fromJson(readText(exchange), JsonElement.class);
                    if (!jsonElement.isJsonObject()) {
                        sendHasIllegal(exchange, "Некорректное тело запроса.");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (!jsonObject.has("status")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"status\".");
                    }
                    if (!jsonObject.has("epicId")) {
                        sendHasInteractions(exchange, "В запросе отсутствует обязательное поле \"epicId\".");
                    }
                    if (!taskManager.isContainedInEpics(jsonObject.get("epicId").getAsInt())) {
                        sendNotFound(exchange, "Эпик с ID: " +
                                jsonObject.get("epicId").getAsInt() + " не найден.");
                    }
                    String containsElement = jsonElement.toString();
                    if ((containsElement.contains("name") &&
                            containsElement.contains("description") &&
                            containsElement.contains("startTime") &&
                            containsElement.contains("duration")) ||
                            (containsElement.contains("name") &&
                                    containsElement.contains("description"))
                    ) {
                        Optional<Integer> subtaskIdOpt = readId(exchange);
                        if (subtaskIdOpt.isEmpty() || !taskManager.isContainedInSubtasks(subtaskIdOpt.get())) {
                            sendNotFound(exchange, "Подзадача не найдена. Пожалуйста, проверьте ID.");
                            return;
                        }
                        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
                        subtask.setTaskType(TaskType.SUBTASK);
                        subtask.setId(subtaskIdOpt.get());
                        taskManager.updateSubtask(subtask);
                        sendTextPost(exchange, "Подзадача успешно обновлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case DELETE_SUBTASK -> {
                try {
                    Optional<Integer> subtaskIdOpt = readId(exchange);
                    if (subtaskIdOpt.isEmpty() || !taskManager.isContainedInSubtasks(subtaskIdOpt.get())) {
                        sendNotFound(exchange, "Подзадача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    taskManager.deleteSubtask(subtaskIdOpt.get());
                    sendText(exchange, "Подзадача с ID: " + subtaskIdOpt.get() + " успешно удалена.");
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case DELETE_SUBTASKS -> {
                taskManager.deleteSubtasks();
                sendText(exchange, "Все подзадачи удалены.");
            }
            case UNKNOWN -> sendHasIllegal(exchange, "Такого эндпоинта не существует");

        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path.length == 2 && path[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_ADDSUBTASK;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASKS;
            }
        } else if (path.length == 3 && path[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATESUBTASK;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
