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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final File file = new File("C:\\Users\\leoni\\IdeaProjects\\java-kanban\\src\\" +
            "ru\\yandex\\practicum\\resourses\\TaskManager.csv");

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(file);
        initTasks();
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

    @Test
    void updateDateTimeTask() {
        taskManager.updateTask(new Task(taskId, "Test addNewTask", "Test addNewTask description",
                "NEW", 30, LocalDateTime.of(2024, 9, 17, 9, 30)));
        taskManager.updateSubtask(new Subtask(subtaskId, "Subtask addNewSubtask",
                "Test addNewSubtask description", "NEW",
                30, LocalDateTime.of(2024, 9, 17, 10, 0), epicId));
        List<String> line = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.ready()) {
                line.add(reader.readLine());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла.", exception);
        }
        assertEquals(4, line.size());
        assertTrue(line.get(0).contains("startTime,duration,endTime"));
        assertTrue(line.get(1).contains("17.09.2024 09:30,30,17.09.2024 10:00"));
        assertTrue(line.get(2).contains("17.09.2024 10:00,30,17.09.2024 10:30"));
        assertTrue(line.get(3).contains("17.09.2024 10:00,30,17.09.2024 10:30,2"));
    }
}
