package Functional.Server;

import Functional.InFile.FileBackedTasksManager;
import JsonAddapters.*;
import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    KVTaskClient client;

    public HTTPTaskManager(String path) {
    super();
    client = new KVTaskClient(path);
    load();
    }

    @Override
    protected void save() {
        List<Task> tasks = new ArrayList<>();
        List<EpicTask> epics = new ArrayList<>();
        List<SubTask> subTasks = new ArrayList<>();
        for (Task task : getTaskList()) {
            if(task.getClass().equals(Task.class)) tasks.add(task);
            if(task.getClass().equals(EpicTask.class)) epics.add((EpicTask) task);
            if(task.getClass().equals(SubTask.class)) subTasks.add((SubTask) task);
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(SubTask.class, new SubtaskAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(EpicTask.class, new EpicAdapter())
                .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
                .create();
        client.put("epic", gson.toJson(epics));
        client.put("task", gson.toJson(tasks));
        client.put("subtask", gson.toJson(subTasks));
        System.out.println(gson.toJson(tasks));
    }


    protected void load() {
        List<Task> tasks;
        List<EpicTask> epics ;
        List<SubTask> subTasks;

        Gson gson = new GsonBuilder().registerTypeAdapter(SubTask.class, new SubtaskAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(EpicTask.class, new EpicAdapter())
                .registerTypeAdapter(SubTask.class, new SubtaskAdapter())
                .create();
        tasks = gson.fromJson(client.load("task"), new TypeToken<List<Task>>() {}.getType());
        epics = gson.fromJson(client.load("epic"), new TypeToken<List<EpicTask>>() {}.getType());
        subTasks = gson.fromJson(client.load("subtask"), new TypeToken<List<SubTask>>() {}.getType());
        if(tasks != null) {
            tasks.forEach(this::addTask);
        }

        if(epics != null) {
            epics.forEach(this::addTask);
        }



        if(subTasks != null) {
            for(SubTask task : subTasks) {
                task.setEpicTask(getEpicById(task.getEpicId()));
            }
            subTasks.forEach(this::addTask);
        }

    }
}
