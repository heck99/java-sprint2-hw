package Functional;

import allTasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    HashMap<Integer, TaskNode> historyMap;
    //если честно мне больше нравится создать отдельный класс для списка, но написано, что делать этого не нужно
    TaskNode tail;
    TaskNode head;

    @Override
    public void add(Task task) {

    }

    @Override
    public void remove(int id) {

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

    private List<Task> getTasks() {
        ArrayList<Task> listToReturn = new ArrayList<>();
        TaskNode currentNode = head;
        while(currentNode != null) {
           listToReturn.add(currentNode.item);
        }
        return listToReturn;
    }

    private void removeNode(TaskNode node) {
        if(node == null) return;
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
    LinkedList
}
