package ru.yandex.practicum.httptest;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.http.BaseHttpHandler;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.TasksManager;

import java.io.File;
import java.io.IOException;


public abstract class HandlersTest<T extends BaseHttpHandler> {

    protected TasksManager taskManager = new FileBackedTaskManager(new File("C:\\Users\\" +
            "leoni\\IdeaProjects\\java-kanban\\src\\ru\\yandex\\practicum\\resourses\\TaskManager.csv"));
    protected HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    protected Gson gson = HttpTaskServer.getGson();

    public HandlersTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void setDown() {
        taskServer.stop();
    }
}
