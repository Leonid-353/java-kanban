package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.FileBackedTaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        File file = new File("C:\\Users\\leoni\\IdeaProjects\\java-kanban\\src\\" +
                "ru\\yandex\\practicum\\resourses\\TaskManager.csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        // Создание
        Task task1 = new Task("Задача 1", "Описание задачи 1", "NEW");
        Task task2 = new Task("Задача 2", "Описание задачи 2", "IN_PROGRESS");
        final int taskId1 = taskManager.addNewTask(task1);
        final int taskId2 = taskManager.addNewTask(task2);
        Task task3 = new Task(taskId1 - 1, "Задача 3", "Описание задачи 3", "DONE");
        final int taskId3 = taskManager.addNewTask(task3);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", "NEW");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", "IN_PROGRESS");
        final int epicId1 = taskManager.addNewEpic(epic1);
        final int epicId2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1-1", "Описание подзадачи 1-1", "NEW", epicId1);
        Subtask subtask2 = new Subtask("Подзадача 2-1",
                "Описание подзадачи 2-1", "IN_PROGRESS", epicId1);
        Subtask subtask3 = new Subtask("Подзадача 3-1", "Описание подзадачи 3-1", "NEW", epicId1);
        Subtask subtask4 = new Subtask("Подзадача 3-2", "Описание подзадачи 3-2", "NEW", epicId2);
        final Integer subtaskId1 = taskManager.addNewSubtask(subtask1);
        final Integer subtaskId2 = taskManager.addNewSubtask(subtask2);
        final Integer subtaskId3 = taskManager.addNewSubtask(subtask3);
        final Integer subtaskId4 = taskManager.addNewSubtask(subtask4);

        FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(taskManager.equals(taskManager1));

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
