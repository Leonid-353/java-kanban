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
    private final TasksManager taskManager = new InMemoryTaskManager(historyManager);

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
        assertNotNull(historyManager.getHead());
        assertNotNull(historyManager.getTail());

        Task task1 = new Task(taskId, "Test addNewTask", "Test addNewTask description", "DONE");

        taskManager.updateTask(task1);
        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не пустая.");

        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", "DONE");

        taskManager.addNewTask(task2);
        historyManager.add(task2);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая");
        assertNotEquals(historyManager.getHead(), historyManager.getTail());
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
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(Status.NEW, epic1.getStatus());

        Epic epic2 = new Epic("Epic addNewEpic2", "Test addNewEpic2 description", "IN_PROGRESS");

        taskManager.addNewEpic(epic2);
        historyManager.add(epic2);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая");
        assertEquals(Status.NEW, epic2.getStatus());
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
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(Status.DONE, subtask1.getStatus());
        assertEquals(Status.DONE, epic.getStatus());

        Subtask subtask2 = new Subtask("Subtask addNewSubtask2",
                "Test addNewSubtask2 description", "NEW", epicId);

        taskManager.addNewSubtask(subtask2);
        historyManager.add(subtask2);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая.");
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void deleteTask() {
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", "DONE");

        taskManager.addNewTask(task2);
        historyManager.add(task);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая");
        assertEquals(2, history.size(), "История не пустая");

        taskManager.deleteTask(task.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void deleteSubtask() {
        Subtask subtask2 = new Subtask("Subtask addNewSubtask2",
                "Test addNewSubtask2 description", "DONE", epicId);

        taskManager.addNewSubtask(subtask2);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая");
        assertEquals(2, history.size(), "История не пустая");
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        taskManager.deleteSubtask(subtask.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void deleteEpic() {
        Subtask subtask2 = new Subtask("Subtask addNewSubtask2",
                "Test addNewSubtask2 description", "DONE", epicId);
        Epic epic1 = new Epic("Epic addNewEpic1", "Test addNewEpic1 description", "IN_PROGRESS");

        taskManager.addNewSubtask(subtask2);
        taskManager.addNewEpic(epic1);
        historyManager.add(epic);
        historyManager.add(epic1);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size());

        taskManager.deleteEpic(epic.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void deleteAllTasks() {
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", "DONE");

        taskManager.addNewTask(task2);
        historyManager.add(task);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());

        taskManager.deleteTasks();
        history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void deleteAllSubtasks() {
        Subtask subtask1 = new Subtask("Subtask addNewSubtask1",
                "Test addNewSubtask1 description", "DONE", epicId);
        Subtask subtask2 = new Subtask("Subtask addNewSubtask2",
                "Test addNewSubtask2 description", "DONE", epicId);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        List<Task> history = historyManager.getHistory();
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(4, history.size());

        taskManager.deleteSubtasks();
        history = historyManager.getHistory();
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(1, history.size());
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Epic addNewEpic1", "Test addNewEpic1 description", "NEW");

        int epic1Id = taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask addNewSubtask1",
                "Test addNewSubtask1 description", "DONE", epic1Id);
        Subtask subtask2 = new Subtask("Subtask addNewSubtask2",
                "Test addNewSubtask2 description", "DONE", epicId);

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        List<Task> history = historyManager.getHistory();
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(Status.DONE, epic1.getStatus());
        assertEquals(6, history.size());

        taskManager.deleteEpics();
        history = historyManager.getHistory();
        assertEquals(1, history.size());
    }
}
