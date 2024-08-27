package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private final File file = new File("C:\\Users\\leoni\\IdeaProjects\\java-kanban\\src\\" +
            "ru\\yandex\\practicum\\resourses\\TaskManager.csv");
    private final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
    private Task task;
    private int taskId;
    private Epic epic;
    private int epicId;
    private Subtask subtask;
    private int subtaskId;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description", "NEW");
        taskId = taskManager.addNewTask(task);
        epic = new Epic("Epic addNewEpic", "Test addNewEpic description", "NEW");
        epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask addNewSubtask", "Test addNewSubtask description",
                "NEW", epicId);
        subtaskId = taskManager.addNewSubtask(subtask);
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager, taskManager1);
    }

    @Test
    void update() {
        Task task1 = new Task(taskId, "Test addNewTask1", "Test addNewTask1 description", "DONE");
        taskManager.updateTask(task1);
        Epic epic1 = new Epic(epicId, "Epic1 addNewEpic",
                "Test addNewEpic newDescription", "DONE");
        taskManager.updateEpic(epic1);
        Subtask subtask1 = new Subtask(subtaskId, "Subtask1 addNewSubtask",
                "Test addNewSubtask newDescription", "IN_PROGRESS", epicId);
        taskManager.updateSubtask(subtask1);
        List<String> line = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                line.add(reader.readLine());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла.", exception);
        }
        assertEquals(4, line.size());
        assertFalse(line.get(2).contains("NEW"));
        assertFalse(line.get(2).contains("DONE"));
        assertTrue(line.get(2).contains("IN_PROGRESS"));
    }
}
