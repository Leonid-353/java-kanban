package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TasksManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    // Получение списка всех задач
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpic(epicId);
        ArrayList<Subtask> epicSubtaskArrayList = new ArrayList<>();
        for (Integer id : epic.getSubtaskId()) {
            epicSubtaskArrayList.add(subtasks.get(id));
        }
        return epicSubtaskArrayList;
    }

    // Удаление всех задач
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : getEpics()) {
            epic.cleanSubtaskId();
            updateEpicStatus(epic);
        }
    }

    // Получение по идентификатору
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    // Создание
    public int addNewTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int addNewEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic.getId();
    }

    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epics.containsKey(subtask.getEpicId())) {
            if (epic == null) {
                System.out.println("No such epic: " + subtask.getEpicId());
                return -1;
            } else {
                subtask.setId(++generatorId);
                epic.addSubtaskId(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic);
            }
        }
        return subtask.getId();
    }

    // Обновление
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (tasks.containsKey(task.getId())) {
            if (!task.equals(oldTask)) {
                tasks.put(task.getId(), task);
            }
        }
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (epics.containsKey(epic.getId())) {
            if (!epic.equals(oldEpic)) {
                for (int id : oldEpic.getSubtaskId()) {
                    epic.addSubtaskId(id);
                }
                epics.put(epic.getId(), epic);
                updateEpicStatus(epic);
            }
        }
    }

    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (subtasks.containsKey(subtask.getId())) {
            if (!subtask.equals(oldSubtask)) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(getEpic(subtask.getEpicId()));
            }
        }
    }

    // Удаление по идентификатору
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        Epic epic = getEpic(epicId);
        for (Integer id : epic.getSubtaskId()) {
            subtasks.remove(id);
        }
        epic.cleanSubtaskId();
        epics.remove(epicId);
    }

    public void deleteSubtask(int subtaskId) {
        Subtask subtask = getSubtask(subtaskId);
        Epic epic = getEpic(subtask.getEpicId());
        subtasks.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic) {
        Set<Status> subtasksStatus = new HashSet<>();
        for (Integer id : epic.getSubtaskId()) {
            subtasksStatus.add(subtasks.get(id).getStatus());
        }
        if (subtasksStatus.isEmpty()) {
            epic.setStatus("NEW");
        } else if (subtasksStatus.size() == 1 && subtasksStatus.contains(Status.NEW)) {
            epic.setStatus("NEW");
        } else if (subtasksStatus.size() == 1 && subtasksStatus.contains(Status.DONE)) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    @Override
    public String toString() {
        return "TasksManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                ", generatorId=" + generatorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksManager manager = (TasksManager) o;
        return generatorId == manager.generatorId &&
                Objects.equals(tasks, manager.tasks) &&
                Objects.equals(epics, manager.epics) &&
                Objects.equals(subtasks, manager.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, epics, subtasks, generatorId);
    }
}