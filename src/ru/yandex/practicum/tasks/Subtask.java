package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Subtask extends Task {

    protected TaskType taskType = TaskType.SUBTASK;
    protected int epicId;

    public Subtask(int id, String name, String description, String status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return id == subtask.id &&
                Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description) &&
                status == subtask.status &&
                epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
