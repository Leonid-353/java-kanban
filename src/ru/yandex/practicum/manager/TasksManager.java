package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface TasksManager {
    // Получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getEpicSubtasks(int epicId);

    // Удаление всех задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    // Получение по идентификатору
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    // Создание
    int addNewTask(Task task) throws ManagerSaveException;

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    // Обновление
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // Удаление по идентификатору
    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    List<Task> getHistory();
}
