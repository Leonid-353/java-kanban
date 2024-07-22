package ru.yandex.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TasksManager taskManager = Managers.getDefault();

    @Test
    public void managerNotNull() {
        assertNotNull(historyManager);
        assertNotNull(taskManager);
    }
}
