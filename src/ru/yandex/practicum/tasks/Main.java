package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.TasksManager;

public class Main {

    public static void main(String[] args) {

        TasksManager manager = new TasksManager();

        // Создание
        Task task1 = new Task("Задача 1", "Описание задачи 1", "NEW");
        Task task2 = new Task("Задача 2", "Описание задачи 2", "IN_PROGRESS");
        final int taskId1 = manager.addNewTask(task1);
        final int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", "NEW");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", "IN_PROGRESS");
        final int epicId1 = manager.addNewEpic(epic1);
        final int epicId2 = manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1-1", "Описание подзадачи 1-1", "NEW", epicId1);
        Subtask subtask2 = new Subtask("Подзадача 2-1", "Описание подзадачи 2-1", "NEW", epicId1);
        Subtask subtask3 = new Subtask("Подзадача 3-1", "Описание подзадачи 3-1", "NEW", epicId1);
        Subtask subtask4 = new Subtask("Подзадача 3-2", "Описание подзадачи 3-2", "NEW", epicId2);
        final Integer subtaskId1 = manager.addNewSubtask(subtask1);
        final Integer subtaskId2 = manager.addNewSubtask(subtask2);
        final Integer subtaskId3 = manager.addNewSubtask(subtask3);
        final Integer subtaskId4 = manager.addNewSubtask(subtask4);

        // Вывод
        for (Epic epicPrint : manager.getEpics()) {
            System.out.println(epicPrint.toString());
        }
        for (Task taskPrint : manager.getTasks()) {
            System.out.println(taskPrint.toString());
        }
        for (Subtask subtaskPrint : manager.getSubtasks()) {
            System.out.println(subtaskPrint.toString());
        }
        System.out.println("----------");

        // Обновление
        manager.updateTask(new Task(taskId1, "Задача 1", "Описание задачи 1", "DONE"));

        manager.updateEpic(new Epic(epicId1, "Эпик 1", "Описание эпика 1", "DONE"));

        manager.updateSubtask(new Subtask(subtaskId2, "Подзадача 2-1",
                "Описание подзадачи 2-1", "DONE", epicId1));

        // Удаление
        manager.deleteSubtask(subtaskId1);

        manager.deleteEpic(epicId1);

        manager.deleteEpics();
    }
}