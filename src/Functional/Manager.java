package Functional;

import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface Manager {

    //пункт 2.1 Получение списка всех задач.
    ArrayList<Task> getTaskList();

    //пункт 2.2 Получение списка всех эпиков.
    ArrayList<EpicTask> getEpicTaskList();

    // пункт 2.3 Получение списка всех подзадач определённого эпика.
    ArrayList<SubTask> getEpicsSubtaskList(int id);

    //пункт 2.4 Получение задачи любого типа по идентификатору
    Task getTaskById(int id);

    //пункт 2.5 Добавление новой задачи, эпика и подзадачи.
    void addTask(Task task);

    //пункт 2.6 Обновление задачи любого типа по идентификатору. Новая версия объекта передаётся в виде параметра.
    void updateTask(Task task);

    //пункт 2.7 Удаление всех задач
    void deleteAllTask();

    //пункт 2.7 Удаление одной задач
    void deleteTask(int id);

    // Получение истории
    List<Task> history();

    EpicTask getEpicById(int id);

    List<Task> getPrioritizedTasks();
}
