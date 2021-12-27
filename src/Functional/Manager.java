package Functional;

import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasksMap;

    public Manager() {
        tasksMap = new HashMap<>();
    }

    //пункт 2.1 Получение списка всех задач.
    public  ArrayList<Task> getTaskList() {
        ArrayList<Task> listToReturn = new ArrayList<>();
        for(int taskId : tasksMap.keySet()) {
            listToReturn.add(tasksMap.get(taskId));
        }
        return listToReturn;
    }

    //пункт 2.2 Получение списка всех эпиков.
    public  ArrayList<EpicTask> getEpicTaskList() {
        ArrayList<EpicTask> listToReturn = new ArrayList<>();
        for(int taskId : tasksMap.keySet()) {
            if(tasksMap.get(taskId).getClass() == EpicTask.class) {
                listToReturn.add((EpicTask) tasksMap.get(taskId));
            }
        }
        return listToReturn;
    }

    // пункт 2.3 Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getEpicsSubtaskList(int id) {
       if(tasksMap.get(id).getClass() == EpicTask.class) {
           return ((EpicTask)tasksMap.get(id)).getSubTasks();
       }
       return null;
    }

    //пункт 2.4 Получение задачи любого типа по идентификатору
    public Task getTaskById(int id) {
        return tasksMap.get(id);
    }

    //пункт 2.5 Добавление новой задачи, эпика и подзадачи.
    public void addTask(Task task) {
        task.setId(getFirstEmptyId());
        tasksMap.put(task.getId(),task);
    }

    //пункт 2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    //Возможно, требуется передавать id объекта и объект, но я не совсем понял, что требует ТЗ, поэтому id уже храниться в Объекте
    public void updateTask(Task task) {
        Task updateTask = tasksMap.get(task.getId());
        if(!task.getName().isEmpty()) updateTask.setName(task.getName());
        if(!task.getDescription().isEmpty()) updateTask.setDescription(task.getDescription());
        updateTask.setStatus(task.getStatus());
        if(updateTask.getClass() == SubTask.class) {
            ((SubTask)updateTask).getEpicTask().checkStatus();
        }
    }

    //пункт 2.7 Удаление всех задач
    public void deleteAllTask() {
            tasksMap.clear();
            //да я очистил всё хранилище, но нужно ли как-то удалять сами объекты?
    }

    //пункт 2.7 Удаление одной задач
    public void deleteTask(int id) {
        Task task = tasksMap.get(id);
        if(task.getClass() == Task.class) tasksMap.remove(id);
        if(task.getClass() == SubTask.class) {
            ((SubTask)task).getEpicTask().deleteSubtask((SubTask)task);
            tasksMap.remove(id);
        }
        if(task.getClass() == EpicTask.class) {
            for(SubTask taskToDelete : ((EpicTask)task).getSubTasks()) {
                tasksMap.remove(taskToDelete.getId());
            }
            tasksMap.remove(id);
        }
    }

    private int getFirstEmptyId() {
        Integer[] keyArr = tasksMap.keySet().toArray(new Integer[0]);
        Arrays.sort(keyArr);
        for(int i = 0;i<keyArr.length; i++) {
            if(i != keyArr[i]) return i;
        }
        return keyArr.length;
    }
}
