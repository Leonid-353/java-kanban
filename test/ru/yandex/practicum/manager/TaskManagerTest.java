package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TasksManager> {

    protected T taskManager = (T) Managers.getDefault();
    protected Task task;
    protected int taskId;
    protected Epic epic;
    protected int epicId;
    protected Subtask subtask;
    protected int subtaskId;

    protected void initTasks() {
        task = new Task("Test addNewTask", "Test addNewTask description", "NEW");
        taskId = taskManager.addNewTask(task);
        epic = new Epic("Epic addNewEpic", "Test addNewEpic description", "DONE");
        epicId = taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask addNewSubtask", "Test addNewSubtask description",
                "NEW", epicId);
        subtaskId = taskManager.addNewSubtask(subtask);
    }

    @BeforeEach
    void setUp() {
        initTasks();
    }

    @Test
    void getTasks() {
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void getEpics() {
        final List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test
    void getSubtasks() {
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
    }

    // Получение списка всех подзадач определённого эпика
    @Test
    void getEpicSubtasks() {
        final List<Subtask> epicSubtask = taskManager.getEpicSubtasks(epic.getId());
        assertEquals(1, epicSubtask.size());
    }

    // Удаление всех задач
    @Test
    void deleteTasks() {
        taskManager.deleteTasks();
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    void deleteEpics() {
        taskManager.deleteEpics();
        final List<Epic> epics = taskManager.getEpics();
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
    }

    @Test
    void deleteSubtasks() {
        taskManager.deleteSubtasks();
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(0, subtasks.size());
    }

    // Получение по идентификатору
    @Test
    void getTask() {
        Task task1 = taskManager.getTask(taskId);
        assertEquals(task, task1);
    }

    @Test
    void getEpic() {
        Epic epic1 = taskManager.getEpic(epicId);
        assertEquals(epic, epic1);
    }

    @Test
    void getSubtask() {
        Subtask subtask1 = taskManager.getSubtask(subtaskId);
        assertEquals(subtask, subtask1);
    }

    // Создание
    @Test
    void addNewTask() throws ManagerSaveException {
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", "DONE");
        int taskId2 = taskManager.addNewTask(task2);
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(2, tasks.size());
        assertEquals(4, taskId2);
    }

    @Test
    void addNewEpic() {
        Epic epic2 = new Epic("Epic addNewEpic2", "Test addNewEpic2 description", "IN_PROGRESS");
        int epicId2 = taskManager.addNewEpic(epic2);
        final List<Epic> epics = taskManager.getEpics();
        assertEquals(2, epics.size());
        assertEquals(4, epicId2);
    }

    @Test
    void addNewSubtask() {
        Subtask subtask2 = new Subtask("Subtask addNewSubtask2", "Test addNewSubtask2 description",
                "DONE", epicId);
        int subtaskId2 = taskManager.addNewSubtask(subtask2);
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(2, subtasks.size());
        assertEquals(4, subtaskId2);
    }

    // Обновление
    @Test
    void updateTask() {
        Task task3 = new Task(taskId, "Test addNewTask3",
                "Test addNewTask3 description", "IN_PROGRESS");
        taskManager.updateTask(task3);
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task3, tasks.getFirst());
    }

    @Test
    void updateEpic() {
        Epic epic3 = new Epic(epicId, "Epic addNewEpic3",
                "Test addNewEpic3 description", "IN_PROGRESS");
        taskManager.updateEpic(epic3);
        final List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic3, epics.getFirst());
    }

    @Test
    void updateSubtask() {
        Subtask subtask3 = new Subtask(subtaskId, "Subtask addNewSubtask3",
                "Test addNewSubtask3 description", "NEW", epicId);
        taskManager.updateSubtask(subtask3);
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask3, subtasks.getFirst());
    }

    // Удаление по идентификатору
    @Test
    void deleteTask() {
        Task task4 = new Task("Test addNewTask4", "Test addNewTask4 description", "IN_PROGRESS");
        int taskId4 = taskManager.addNewTask(task4);
        taskManager.deleteTask(taskId);
        final List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task4, tasks.getFirst());
        assertEquals(4, taskId4);
    }

    @Test
    void deleteEpic() {
        Epic epic4 = new Epic("Epic addNewEpic4", "Test addNewEpic4 description", "NEW");
        int epicId4 = taskManager.addNewEpic(epic4);
        taskManager.deleteEpic(epicId);
        final List<Epic> epics = taskManager.getEpics();
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, epics.size());
        assertEquals(epic4, epics.getFirst());
        assertEquals(4, epicId4);
        assertEquals(0, subtasks.size());
    }

    @Test
    void deleteSubtask() {
        Subtask subtask4 = new Subtask("Subtask addNewSubtask2", "Test addNewSubtask2 description",
                "DONE", epicId);
        int subtaskId4 = taskManager.addNewSubtask(subtask4);
        taskManager.deleteSubtask(subtaskId);
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask4, subtasks.getFirst());
        assertEquals(4, subtaskId4);
    }

    @Test
    void getHistory() {
        Task task5 = taskManager.getTask(taskId);
        Epic epic5 = taskManager.getEpic(epicId);
        Subtask subtask5 = taskManager.getSubtask(subtaskId);
        final List<Task> historyTasks = taskManager.getHistory();
        assertEquals(3, historyTasks.size());
    }
}
