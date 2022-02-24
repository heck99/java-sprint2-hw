package Functional;

import allTasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    HashMap<Integer, TaskNode> historyMap;
    //если честно мне больше нравится создать отдельный класс для списка, но написано, что делать этого не нужно
    TaskNode tail;
    TaskNode head;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
    }

    @Override
    public String toString() {
        String stringToReturn = "";
        TaskNode iterator = head;
        while (iterator!=null) {
            stringToReturn += String.valueOf(iterator.item.getId());
            iterator = iterator.next;
        }
        return stringToReturn;
    }

    @Override
    public void add(Task task) {
        int taskId = task.getId();
        if(historyMap.containsKey(taskId)) {
            removeNode(historyMap.get(taskId));
            historyMap.remove(taskId);
        }
        linkLast(task);
        historyMap.put(tail.item.getId(),tail);
    }

    @Override
    public void remove(int id) {
        if(!historyMap.containsKey(id)) return;
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        TaskNode t = tail;
        TaskNode newNode = new TaskNode(t, task, null);
        tail = newNode;
        if (t == null)
            head = newNode;
        else
            t.next = newNode;
    }

    @Override
    public void clear() {
        tail=null;
        head=null;
        historyMap.clear();
    }

    private List<Task> getTasks() {
        ArrayList<Task> listToReturn = new ArrayList<>();
        TaskNode currentNode = head;
        while(currentNode != null) {
           listToReturn.add(currentNode.item);
           currentNode = currentNode.next;
        }
        return listToReturn;
    }

    private void removeNode(TaskNode node) {
        if(node == null) return;
        if(node.prev == null && node.next == null) {
            tail = null;
            head = null;
            return;
        }
        if(node.prev == null) {
            head = node.next;
            node.next.prev = null;
            return;
        }
        if(node.next == null) {
            tail = node.prev;
            node.prev.next = null;
            return;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }


    private  class TaskNode {
        Task item;
        TaskNode next;
        TaskNode prev;

        TaskNode(TaskNode prev, Task element, TaskNode next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}
