package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskType;

import java.io.*;
import java.util.Objects;

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
                    taskManager.addTask(task);
                    generatorId++;
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
            writer.write("id,type,name,status,description,epic,");
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
        } else if (task.getTaskType().equals(TaskType.EPIC)) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
            subtasks.put(task.getId(), (Subtask) task);
            epics.get(task.getEpicId()).addSubtaskId(task.getId());
        }
    }

    //Добавление
    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask.getId();
    }

    //Обновление
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTaskManager manager = (FileBackedTaskManager) o;
        return file == manager.file &&
                Objects.equals(tasks, manager.tasks) &&
                Objects.equals(epics, manager.epics) &&
                Objects.equals(subtasks, manager.subtasks);
    }

    @Override
    public String toString() {
        return "FileBackedTaskManager{" +
                "file=" + file +
                ", tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file);
    }
}
