package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskType;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int generatorId = 0;
            while (reader.ready()) {
                Task task = CSVTaskFormat.taskFromString(reader.readLine());
                if (task != null) {
                    try {
                        taskManager.addTask(task);
                        generatorId++;
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            taskManager.setGeneratorId(generatorId);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла.", exception);
        }
        return taskManager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,endTime,epic,");
            writer.newLine();
            for (int i = 1; i <= getGeneratorId(); i++) {
                if (tasks.containsKey(i)) {
                    writer.write(CSVTaskFormat.taskToString(tasks.get(i)));
                    writer.newLine();
                } else if (epics.containsKey(i)) {
                    writer.write(CSVTaskFormat.taskToString(epics.get(i)));
                    writer.newLine();
                } else if (subtasks.containsKey(i)) {
                    writer.write(CSVTaskFormat.taskToString(subtasks.get(i)));
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи в файл.", exception);
        }
    }

    private void addTask(Task task) {
        if (task.getTaskType().equals(TaskType.TASK)) {
            tasks.put(task.getId(), task);
            add(task);
        } else if (task.getTaskType().equals(TaskType.EPIC)) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
            subtasks.put(task.getId(), (Subtask) task);
            epics.get(task.getEpicId()).addSubtaskId(task.getId());
            updateEpicTime(epics.get(task.getEpicId()));
            add(task);
        }
    }

    //Добавление
    @Override
    public int addNewTask(Task task) {
        if (task != null) {
            super.addNewTask(task);
            save();
            return task.getId();
        }
        return -1;
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic != null) {
            super.addNewEpic(epic);
            save();
            return epic.getId();
        }
        return -1;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            super.addNewSubtask(subtask);
            save();
            return subtask.getId();
        }
        return -1;
    }

    //Обновление
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            super.updateTask(task);
            save();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            super.updateEpic(epic);
            save();
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            super.updateSubtask(subtask);
            save();
        }
    }

    //Удаление по идентификатору
    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    //Удаление
    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
}
