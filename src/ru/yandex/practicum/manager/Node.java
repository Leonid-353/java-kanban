package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}
