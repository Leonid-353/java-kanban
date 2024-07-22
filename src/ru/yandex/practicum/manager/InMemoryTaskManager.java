package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TasksManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtaskArrayList = new ArrayList<>();
        for (Integer id : epic.getSubtaskId()) {
            epicSubtaskArrayList.add(subtasks.get(id));
        }
        return epicSubtaskArrayList;
    }

    // Удаление всех задач
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : getEpics()) {
            epic.cleanSubtaskId();
            updateEpicStatus(epic);
        }
    }

    // Получение по идентификатору
    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    // Создание
    @Override
    public int addNewTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
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
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
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

    @Override
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
    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer id : epic.getSubtaskId()) {
            subtasks.remove(id);
        }
        epic.cleanSubtaskId();
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        InMemoryTaskManager manager = (InMemoryTaskManager) o;
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
