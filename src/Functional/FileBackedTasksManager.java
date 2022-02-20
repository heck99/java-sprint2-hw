package Functional;

import allTasks.Task;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileBackedTasksManager extends InMemoryTasksManager{
    String path;

    public FileBackedTasksManager(String path) {
        super();
        this.path = path;
    }

    public FileBackedTasksManager() {
        super();
    }

    private void save() {
        //Всегда ли нужно указывать кодировку при создании FileWriter/FileReader
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            bw.write(String.join(",","id", "type", "name", "status", "description", "epic"));
            for (Integer taskId : tasksMap.keySet()) {
                bw.newLine();
                bw.write(tasksMap.get(taskId).toString(1));
            }
            bw.newLine();
            bw.write(historyManager.toString());
        } catch (IOException ex) {

        }

    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }
}
