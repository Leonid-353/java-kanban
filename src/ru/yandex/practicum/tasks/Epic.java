package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    protected List<Integer> subtaskId = new ArrayList<>();

    public Epic(int id, String name, String description, String status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, String status) {
        super(name, description, status);
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
    public String toString() {
        return "Epic{" +
                "subtaskId=" + subtaskId +
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
        Epic epic = (Epic) o;
        return id == epic.id &&
                Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                status == epic.status &&
                Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }
}
