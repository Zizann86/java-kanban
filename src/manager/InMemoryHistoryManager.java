package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> historyTasksForMap = new HashMap<>();

    @Override
    public void addToHistory(Task task) {
        if (historyTasksForMap.containsKey(task.getId())) {
            removeNode(historyTasksForMap.get(task.getId()));
        }
        Node node = new Node(null, task, null);
        linkLast(node);
        historyTasksForMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (historyTasksForMap.containsKey(id)) {
            removeNode(historyTasksForMap.remove(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

   private void linkLast(Node node) {
        final Node oldTail = tail;
        node.prev = oldTail;
        tail = node;
        if (oldTail == null) {
            head = node;
        } else {
            oldTail.next = node;
        }
    }

   private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            head = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

   private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    //Создаем вложенный класс Node:
    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        private Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}
