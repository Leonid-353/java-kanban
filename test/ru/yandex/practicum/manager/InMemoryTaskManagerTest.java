package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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
        epic = new Epic("Epic addNewEpic", "Test addNewEpic description", "DONE");
        epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask addNewSubtask", "Test addNewSubtask description",
                "DONE", epicId);
        subtaskId = taskManager.addNewSubtask(subtask);
    }

    @Test
    public void addNewTask() {

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void addNewSubtask() {

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
        assertEquals(Status.DONE, epic.getStatus());

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void givenIdGeneratedId() {
        Task task1 = new Task(taskId, "Test addNewTask", "Test addNewTask description", "DONE");
        taskManager.addNewTask(task1);
        assertEquals(4, task1.getId());
    }

    @Test
    public void taskConstancy() {
        assertEquals(1, task.getId());
        assertEquals("Test addNewTask", task.getName());
        assertEquals("Test addNewTask description", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    public void epicConstancy() {
        assertEquals(2, epic.getId());
        assertEquals("Epic addNewEpic", epic.getName());
        assertEquals("Test addNewEpic description", epic.getDescription());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void subtaskConstancy() {
        assertEquals(3, subtask.getId());
        assertEquals("Subtask addNewSubtask", subtask.getName());
        assertEquals("Test addNewSubtask description", subtask.getDescription());
        assertEquals(Status.DONE, subtask.getStatus());
        assertEquals(epicId, subtask.getEpicId());
    }

    @Test
    public void taskDelete() {
        taskManager.deleteTask(taskId);
        boolean value = taskManager.getTasks().contains(task);
        assertFalse(value);
    }

    @Test
    public void epicDelete() {
        taskManager.deleteEpic(epicId);
        boolean valueEpic = taskManager.getEpics().contains(epic);
        boolean valueSubtask = taskManager.getSubtasks().contains(subtask);
        assertFalse(valueEpic);
        assertFalse(valueSubtask);
    }

    @Test
    public void subtaskDelete() {
        taskManager.deleteSubtask(subtaskId);
        boolean value = taskManager.getSubtasks().contains(subtask);
        assertFalse(value);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void allTasksDelete() {
        taskManager.addNewTask(new Task("Test addNewTask", "Test addNewTask description",
                "DONE"));
        assertEquals(2, taskManager.getTasks().size());
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void allEpicsDelete() {
        taskManager.addNewEpic(new Epic("Test addNewEpic", "Test addNewEpic description",
                "NEW"));
        assertEquals(2, taskManager.getEpics().size());
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void allSubtasksDelete() {
        taskManager.addNewSubtask(new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                "NEW", epicId));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteSubtasks();
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(Status.NEW, epic.getStatus());
    }
}
