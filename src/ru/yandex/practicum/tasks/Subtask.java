package ru.yandex.practicum.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    protected TaskType taskType = TaskType.SUBTASK;
    protected int epicId;

    public Subtask(String name, String description, String status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, String status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, String status,
                   long duration, LocalDateTime startTime, int epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, String status,
                   long duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, status, duration, startTime);
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
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
                epicId == subtask.epicId &&
                Objects.equals(startTime, subtask.startTime) &&
                Objects.equals(duration, subtask.duration) &&
                Objects.equals(getEndTime(), subtask.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
