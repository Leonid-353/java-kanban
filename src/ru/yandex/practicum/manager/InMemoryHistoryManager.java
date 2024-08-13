package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    private final Map<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        linkLast(task);
    }

    private void linkLast(Task task) {
        Node node = new Node(task, null, null);
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        if (nodeMap.isEmpty()) {
            head = node;
        } else {
            node.prev = tail;
            tail.next = node;
        }
        tail = node;
        nodeMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        final Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node.prev == null && node.next != null) {
            head = node.next;
            node.next.prev = null;
        } else if (node.next == null && node.prev != null) {
            tail = node.prev;
            node.prev.next = null;
        }
        if (nodeMap.isEmpty()) {
            head = null;
            tail = null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getHistoryTasks();
    }

    private List<Task> getHistoryTasks() {
        final List<Task> historyTasks = new ArrayList<>();
        if (head != null) {
            Node node = head;
            while (node.task != null) {
                historyTasks.add(node.task);
                node = node.next;
                if (node == null) {
                    break;
                }
            }
        }
        return historyTasks;
    }

    public Node getTail() {
        return tail;
    }

    public Node getHead() {
        return head;
    }
}
