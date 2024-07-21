package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TasksManager taskManager = Managers.getDefault();

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
    void addTask() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

        Task task1 = new Task(taskId, "Test addNewTask", "Test addNewTask description", "DONE");

        taskManager.updateTask(task1);
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая.");
    }

    @Test
    void addEpic() {
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

        Epic epic1 = new Epic(epicId, "Epic addNewEpic", "Test addNewEpic description", "DONE");

        taskManager.updateEpic(epic1);
        historyManager.add(epic1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void addSubtask() {
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");

        Subtask subtask1 = new Subtask(subtaskId, "Subtask addNewSubtask",
                "Test addNewSubtask description", "DONE", epicId);

        taskManager.updateSubtask(subtask1);
        historyManager.add(subtask1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(Status.DONE, subtask1.getStatus());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void maxSize() {
        for (int i = 0; i < historyManager.getMaxHistoryLength(); i++) {
            historyManager.add(task);
        }
        assertEquals(historyManager.getMaxHistoryLength(), historyManager.getHistory().size());
        historyManager.add(epic);
        assertEquals(historyManager.getMaxHistoryLength(), historyManager.getHistory().size());
        assertEquals(historyManager.getHistory().getLast(), epic);
    }

}