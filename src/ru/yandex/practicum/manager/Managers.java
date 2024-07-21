package ru.yandex.practicum.manager;

public class Managers {

    private Managers() {}

    public static TasksManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
