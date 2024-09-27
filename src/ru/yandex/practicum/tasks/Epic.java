package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected TaskType taskType = TaskType.EPIC;
    protected List<Integer> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, String status) {
        super(id, name, description, status);
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    public void addSubtaskId(int id) {
        subtaskId.add(id);
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void cleanSubtaskId() {
        subtaskId.clear();
    }

    public void removeSubtaskId(int id) {
        subtaskId.remove(Integer.valueOf(id));
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskId=" + subtaskId +
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
        Epic epic = (Epic) o;
        return id == epic.id &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                status == epic.status &&
                Objects.equals(subtaskId, epic.subtaskId) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(duration, epic.duration) &&
                Objects.equals(getEndTime(), epic.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }
}
