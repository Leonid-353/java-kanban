package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TasksManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks;
    private final HistoryManager historyManager;

    private int generatorId = 0;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(((Comparator<Task>) (t1, t2) -> {
            if (t1.equals(t2)) {
                return 0;
            }
            if (t1.getStartTime() == null) {
                return -1;
            }
            if (t2.getStartTime() == null) {
                return 1;
            }
            if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            } else {
                return -1;
            }
        })
                .thenComparingInt(Task::getId));
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
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            historyManager.remove(entry.getValue().getId());
        }
        tasks.clear();
        prioritizedTasks.removeIf(task -> task.getTaskType().equals(TaskType.TASK));
    }

    @Override
    public void deleteEpics() {
        for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
            historyManager.remove(entry.getValue().getId());
            for (Integer id : entry.getValue().getSubtaskId()) {
                historyManager.remove(id);
            }
        }
        epics.clear();
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getTaskType().equals(TaskType.SUBTASK));
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : getEpics()) {
            for (Integer id : epic.getSubtaskId()) {
                historyManager.remove(id);
                subtasks.remove(id);
            }
            epic.cleanSubtaskId();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        prioritizedTasks.removeIf(task -> task.getTaskType().equals(TaskType.SUBTASK));
    }

    // Получение по идентификатору

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
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
        add(task);
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        updateEpicTime(epic);
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
                add(subtask);
                subtask.setId(++generatorId);
                epic.addSubtaskId(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
        }
        return subtask.getId();
    }

    // Обновление

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (prioritizedTasks.contains(tasks.get(task.getId()))) {
                prioritizedTasks.remove(tasks.get(task.getId()));
                add(task);
            }
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
                updateEpicStatus(epic);
                updateEpicTime(epic);
                epics.put(epic.getId(), epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        Epic subtaskEpic = epics.get(subtask.getEpicId());
        if (subtasks.containsKey(subtask.getId())) {
            if (!subtask.equals(oldSubtask)) {
                if (prioritizedTasks.contains(oldSubtask)) {
                    prioritizedTasks.remove(oldSubtask);
                    add(subtask);
                }
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(subtaskEpic);
                updateEpicTime(subtaskEpic);
            }
        }
    }

    // Удаление по идентификатору

    @Override
    public void deleteTask(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        getEpicSubtasks(epicId).forEach(prioritizedTasks::remove);
        for (Integer id : epic.getSubtaskId()) {
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epic.cleanSubtaskId();
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtaskId);
        historyManager.remove(subtaskId);
        epic.removeSubtaskId(subtaskId);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    // Проверка наличия по id

    @Override
    public boolean isContainedInTasks(int taskId) {
        return tasks.containsKey(taskId);
    }

    @Override
    public boolean isContainedInEpics(int epicId) {
        return epics.containsKey(epicId);
    }

    @Override
    public boolean isContainedInSubtasks(int subtaskId) {
        return subtasks.containsKey(subtaskId);
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

    protected void updateEpicTime(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        } else {
            List<Subtask> subtasks1 = getEpicSubtasks(epic.getId());
            List<Subtask> sorted = getEpicSubtasks(epic.getId()).stream()
                    .filter(subtask -> subtask.getStartTime() != null)
                    .sorted(Comparator.comparing(Task::getStartTime).thenComparingInt(Task::getId))
                    .toList();
            if (!sorted.isEmpty()) {
                epic.setStartTime(sorted.getFirst().getStartTime());
                epic.setEndTime(sorted.getLast().getEndTime());
                epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
            } else {
                epic.setStartTime(null);
                epic.setDuration(null);
                epic.setEndTime(null);
            }
        }
    }

    protected void add(Task task) {
        if (task.getStartTime() != null) {
            if (getPrioritizedTasks().stream().anyMatch(t -> checkingIntersects(t, task))) {
                throw new IllegalArgumentException("Задача пересекается по времени с уже существующей.");
            }
        }
        prioritizedTasks.add(task);
    }

    private boolean checkingIntersects(Task t, Task task) {
        return !((task.getEndTime().isBefore(t.getStartTime()) || task.getEndTime().isEqual(t.getStartTime()))
                || (task.getStartTime().isAfter(t.getEndTime()) || task.getStartTime().isEqual(t.getEndTime())));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedList = new ArrayList<>();
        prioritizedTasks.stream()
                .map(task -> {
                    if (Objects.nonNull(task.getStartTime())) {
                        prioritizedList.add(task);
                    }
                    return null;
                })
                .collect(Collectors.toSet());
        return prioritizedList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public int getGeneratorId() {
        return generatorId;
    }

    protected void setGeneratorId(int generatorId) {
        this.generatorId = generatorId;
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
