package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyTasks = new ArrayList<>();

    private static final int MAX_HISTORY_LENGTH = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        historyTasks.add(task);
        if (historyTasks.size() > MAX_HISTORY_LENGTH) {
            historyTasks.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTasks);
    }

    public int getMaxHistoryLength() {
        return MAX_HISTORY_LENGTH;
    }
}
