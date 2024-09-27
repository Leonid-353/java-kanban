package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected TaskType taskType = TaskType.TASK;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.duration = null;
        this.startTime = null;
    }

    public Task(int id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.duration = null;
        this.startTime = null;
    }

    public Task(String name, String description, String status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(int id, String name, String description, String status, long duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public boolean isEpic() {
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEpicId() {
        return -1;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration == null || startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
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
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status &&
                Objects.equals(startTime, task.startTime) &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(getEndTime(), task.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }
}
