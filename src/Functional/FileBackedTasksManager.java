package Functional;

import allTasks.*;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
            for (Task task :this.getTaskList()) {
                bw.newLine();
                bw.write(task.toString(1));
            }
            bw.newLine();
            String str = "";
            for (Task task : history()) {
                str += task.getId() + ",";
            }
            if(!str.isEmpty()) {
                bw.write(str.substring(0, str.length() - 1));
            }
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
    private Task fromString(String value) {
        String[] fields = value.split(",");
        Task taskToReturn;
        switch (TaskType.valueOf(fields[1])) {
            case TASK:
                taskToReturn = new Task(fields[2], fields[4], Status.valueOf(fields[3]));
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            case EPIC:
                taskToReturn = new EpicTask(fields[2], fields[4]);
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            case SUBTASK:
                taskToReturn = new SubTask(fields[2], fields[4], Status.valueOf(fields[3]),
                        this.getEpicById(Integer.parseInt(fields[5])));
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            default:
                return null;
        }
        return taskToReturn;
    }
}
