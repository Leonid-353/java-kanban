package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class CSVTaskFormat {

    static final DateTimeFormatter formatterStartEnd = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private CSVTaskFormat() {
    }

    public static String taskToString(Task task) {
        String string = task.getId() + "," +
                task.getTaskType() + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + ",";
        if (task.getStartTime() != null) {
            string = string +
                    task.getStartTime().format(formatterStartEnd) + "," +
                    task.getDuration().toMinutes() + "," +
                    task.getEndTime().format(formatterStartEnd) + ",";
        } else {
            string = string +
                    "null," +
                    "null," +
                    "null,";
        }
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            string = string + task.getEpicId() + ",";
        }
        return string;
    }

    public static Task taskFromString(String value) {
        Task task = null;
        if (!value.equals("id,type,name,status,description,startTime,duration,endTime,epic,")) {
            final String[] values = value.split(",");
            final int id = Integer.parseInt(values[0]);
            final TaskType taskType = TaskType.valueOf(values[1]);
            final String name = values[2];
            final String description = values[4];
            final String status = values[3];
            if (!values[5].equals("null")) {
                final LocalDateTime startTime = LocalDateTime.parse(values[5], formatterStartEnd);
                final long duration = Long.parseLong(values[6]);
                final LocalDateTime endTime = LocalDateTime.parse(values[7], formatterStartEnd);
                if (taskType.equals(TaskType.TASK)) {
                    task = new Task(id, name, description, status, duration, startTime);
                } else if (taskType.equals(TaskType.EPIC)) {
                    task = new Epic(id, name, description, status);
                } else if (taskType.equals(TaskType.SUBTASK)) {
                    final int epicId = Integer.parseInt(values[8]);
                    task = new Subtask(id, name, description, status, duration, startTime, epicId);
                }
            } else {
                if (taskType.equals(TaskType.TASK)) {
                    task = new Task(id, name, description, status);
                } else if (taskType.equals(TaskType.EPIC)) {
                    task = new Epic(id, name, description, status);
                } else if (taskType.equals(TaskType.SUBTASK)) {
                    final int epicId = Integer.parseInt(values[8]);
                    task = new Subtask(id, name, description, status, epicId);
                }
            }

        }
        return task;
    }
}
