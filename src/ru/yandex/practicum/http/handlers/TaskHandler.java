package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.manager.TasksManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskType;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final Gson gson;
    private final TasksManager taskManager;

    public TaskHandler(Gson gson, TasksManager taskManager) {
        this.gson = gson;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK -> {
                try {
                    Optional<Integer> taskIdOpt = readId(exchange);
                    if (taskIdOpt.isEmpty() || !taskManager.isContainedInTasks(taskIdOpt.get())) {
                        sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    String response = gson.toJson(taskManager.getTask(taskIdOpt.get()));
                    sendText(exchange, response);
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case GET_TASKS -> {
                try {
                    sendText(exchange, gson.toJson(taskManager.getTasks()));
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case POST_ADDTASK -> {
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
                        Task task = gson.fromJson(jsonObject, Task.class);
                        task.setTaskType(TaskType.TASK);
                        taskManager.addNewTask(task);
                        sendTextPost(exchange, "Задача успешно добавлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case POST_UPDATETASK -> {
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
                        Optional<Integer> taskIdOpt = readId(exchange);
                        if (taskIdOpt.isEmpty() || !taskManager.isContainedInTasks(taskIdOpt.get())) {
                            sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                            return;
                        }
                        Task task = gson.fromJson(jsonObject, Task.class);
                        task.setTaskType(TaskType.TASK);
                        task.setId(taskIdOpt.get());
                        taskManager.updateTask(task);
                        sendTextPost(exchange, "Задача успешно обновлена.");
                    } else {
                        sendHasInteractions(exchange, "Заполнены не все поля.");
                    }
                } catch (IllegalArgumentException exception) {
                    sendHasInteractions(exchange, exception.getMessage());
                }
            }
            case DELETE_TASK -> {
                try {
                    Optional<Integer> taskIdOpt = readId(exchange);
                    if (taskIdOpt.isEmpty() || !taskManager.isContainedInTasks(taskIdOpt.get())) {
                        sendNotFound(exchange, "Задача не найдена. Пожалуйста, проверьте ID.");
                        return;
                    }
                    taskManager.deleteTask(taskIdOpt.get());
                    sendText(exchange, "Задача с ID: " + taskIdOpt.get() + " успешно удалена.");
                } catch (Exception exception) {
                    sendHasIllegal(exchange, exception.getMessage());
                }
            }
            case DELETE_TASKS -> {
                taskManager.deleteTasks();
                sendText(exchange, "Все задачи удалены.");
            }
            case UNKNOWN -> sendHasIllegal(exchange, "Такого эндпоинта не существует");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (path.length == 2 && path[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_ADDTASK;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASKS;
            }
        } else if (path.length == 3 && path[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_UPDATETASK;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
