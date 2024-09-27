package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.FileBackedTaskManager;

import java.io.File;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        File file = new File("C:\\Users\\leoni\\IdeaProjects\\java-kanban\\src\\" +
                "ru\\yandex\\practicum\\resourses\\TaskManager.csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        // Создание
        Task task1 = new Task("Задача 1", "Описание задачи 1", "NEW",
                10L, LocalDateTime.of(2024, 9, 9, 21, 0));
        Task task2 = new Task("Задача 2", "Описание задачи 2", "IN_PROGRESS",
                30L, LocalDateTime.of(2024, 9, 10, 5, 0));
        final int taskId1 = taskManager.addNewTask(task1);
        final int taskId2 = taskManager.addNewTask(task2);
        System.out.println(taskManager.getPrioritizedTasks());
        Task task3 = new Task(taskId1 - 1, "Задача 3", "Описание задачи 3", "DONE");
        final int taskId3 = taskManager.addNewTask(task3);
        try {
            Task task4 = new Task("Задача 4", "Описание задачи 4", "IN_PROGRESS",
                    30L, LocalDateTime.of(2024, 9, 10, 4, 45));
            final int taskId4 = taskManager.addNewTask(task4);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(taskManager.getPrioritizedTasks());
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", "NEW");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", "IN_PROGRESS");
        final int epicId1 = taskManager.addNewEpic(epic1);
        final int epicId2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1-1", "Описание подзадачи 1-1", "NEW",
                15L, LocalDateTime.of(2024, 9, 17, 10, 0), epicId1);
        Subtask subtask2 = new Subtask("Подзадача 2-1",
                "Описание подзадачи 2-1", "IN_PROGRESS",
                15L, LocalDateTime.of(2024, 9, 17, 10, 15), epicId1);
        Subtask subtask3 = new Subtask("Подзадача 3-1", "Описание подзадачи 3-1", "NEW",
                15L, LocalDateTime.of(2024, 9, 17, 10, 30), epicId1);
        Subtask subtask4 = new Subtask("Подзадача 3-2", "Описание подзадачи 3-2", "NEW",
                15L, LocalDateTime.of(2024, 9, 17, 10, 45), epicId2);
        final Integer subtaskId1 = taskManager.addNewSubtask(subtask1);
        final Integer subtaskId2 = taskManager.addNewSubtask(subtask2);
        final Integer subtaskId3 = taskManager.addNewSubtask(subtask3);
        final Integer subtaskId4 = taskManager.addNewSubtask(subtask4);

        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(taskManager.equals(taskManager1));

        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println(taskManager1.getPrioritizedTasks());

        //История
        taskManager1.getTask(taskId1);
        taskManager1.getTask(taskId1);
        taskManager1.getTask(taskId2);
        taskManager1.getTask(taskId3);
        taskManager1.updateTask(new Task(taskId1, "Задача 1", "Описание задачи 1", "DONE"));
        taskManager1.getTask(taskId1);
        taskManager1.getEpic(epicId1);
        taskManager1.getEpic(epicId2);
        taskManager1.getSubtask(subtaskId1);
        taskManager1.getSubtask(subtaskId2);
        taskManager1.getSubtask(subtaskId3);
        taskManager1.getSubtask(subtaskId4);
        for (Task task : taskManager1.getHistory()) {
            System.out.println(task);
        }

        // Вывод
        for (Epic epicPrint : taskManager1.getEpics()) {
            System.out.println(epicPrint.toString());
        }
        for (Task taskPrint : taskManager1.getTasks()) {
            System.out.println(taskPrint.toString());
        }
        for (Subtask subtaskPrint : taskManager1.getSubtasks()) {
            System.out.println(subtaskPrint.toString());
        }
        System.out.println("----------");

        // Удаление
        taskManager1.deleteSubtasks();
        for (Task task : taskManager1.getHistory()) {
            System.out.println(task);
        }
    }
}
