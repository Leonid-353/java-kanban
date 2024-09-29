package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.http.handlers.*;
import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TasksManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public static Gson gson;
    private final HttpServer httpServer;
    private final TasksManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TasksManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(gson, this.taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(gson, this.taskManager));
        httpServer.createContext("/epics", new EpicHandler(gson, this.taskManager));
        httpServer.createContext("/history", new HistoryHandler(gson, this.taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(gson, this.taskManager));
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer(FileBackedTaskManager.loadFromFile(new File("C:\\Users\\" +
                "leoni\\IdeaProjects\\java-kanban\\src\\ru\\yandex\\practicum\\resourses\\TaskManager.csv")));
        server.start();
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }
}
