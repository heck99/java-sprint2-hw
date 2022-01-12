package Functional;

import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public interface Manger {

    //пункт 2.1 Получение списка всех задач.
    public ArrayList<Task> getTaskList();

    //пункт 2.2 Получение списка всех эпиков.
    public  ArrayList<EpicTask> getEpicTaskList();

    // пункт 2.3 Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getEpicsSubtaskList(int id);

    //пункт 2.4 Получение задачи любого типа по идентификатору
    public Task getTaskById(int id);

    //пункт 2.5 Добавление новой задачи, эпика и подзадачи.
    public void addTask(Task task);

    //пункт 2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    public void updateTask(Task task);

    //пункт 2.7 Удаление всех задач
    public void deleteAllTask();

    //пункт 2.7 Удаление одной задач
    public void deleteTask(int id);

    public ArrayList<Task> history();
}
