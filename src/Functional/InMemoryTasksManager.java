package Functional;

import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InMemoryTasksManager implements Manger {
    private HashMap<Integer, Task> tasksMap;

    /*возможно стоит объявить менеджера историй в главной программе и передавать в функции в качестве аргумента, но
    мне кажется, более логичным сделать так, потому что история это часть работы с задачами, и взаимодействие c
    history manager таким образом мне кажется более логична, чем передача его в метод getTaskById, как параметр*/
    private HistoryManager historyManager;

    public InMemoryTasksManager() {
        tasksMap = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
    }

    //пункт 2.1 Получение списка всех задач.
    @Override
    public  ArrayList<Task> getTaskList() {
        ArrayList<Task> listToReturn = new ArrayList<>();
        for(int taskId : tasksMap.keySet()) {
            listToReturn.add(tasksMap.get(taskId));
        }
        return listToReturn;
    }

    @Override
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

    @Override
    // пункт 2.3 Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getEpicsSubtaskList(int id) {
       if(tasksMap.get(id).getClass() == EpicTask.class) {
           return ((EpicTask)tasksMap.get(id)).getSubTasks();
       }
       return null;
    }

    @Override
    //пункт 2.4 Получение задачи любого типа по идентификатору
    public Task getTaskById(int id) {
        historyManager.add(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    //пункт 2.5 Добавление новой задачи, эпика и подзадачи.
    public void addTask(Task task) {
        task.setId(getFirstEmptyId());
        tasksMap.put(task.getId(),task);
    }

    @Override
    //пункт 2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    public void updateTask(Task task) {
        Task updateTask = tasksMap.get(task.getId());
        if(!task.getName().isEmpty()) updateTask.setName(task.getName());
        if(!task.getDescription().isEmpty()) updateTask.setDescription(task.getDescription());
        updateTask.setStatus(task.getStatus());
        if(updateTask.getClass() == SubTask.class) {
            ((SubTask)updateTask).getEpicTask().checkStatus();
        }
    }

    @Override
    //пункт 2.7 Удаление всех задач
    public void deleteAllTask() {
        historyManager.clear();
            tasksMap.clear();
            //да я очистил всё хранилище, но нужно ли как-то удалять сами объекты?
    }

    @Override
    //пункт 2.7 Удаление одной задач
    public void deleteTask(int id) {
        Task task = tasksMap.get(id);
        historyManager.remove(id);
        if(task.getClass() == Task.class) tasksMap.remove(id);
        if(task.getClass() == SubTask.class) {
            ((SubTask)task).getEpicTask().deleteSubtask((SubTask)task);
            tasksMap.remove(id);
        }
        if(task.getClass() == EpicTask.class) {
            for(SubTask taskToDelete : ((EpicTask)task).getSubTasks()) {
                historyManager.remove(taskToDelete.getId());
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

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public EpicTask getEpicById(int id) {
        Task epic = tasksMap.get(id);
        if(epic.getClass() == EpicTask.class) {
            return (EpicTask) epic;
        }
        return null;
    }
}
