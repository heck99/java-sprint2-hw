package Functional;

import allTasks.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileBackedTasksManager extends InMemoryTasksManager{
    String path;

    public FileBackedTasksManager(String path) {
        super();
        this.path = path;
        loadFromFile();
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

    private void historyFromString(String data) {
        String[] tasksId = data.split(",");
        for (String id : tasksId) {
            getTaskById(Integer.parseInt(id));
        }
    }

    private void loadFromFile() {
        try {
            String data = Files.readString(Path.of(path));
            String[] strings = data.split("\\r\\n");

            for (int i = 1; i < strings.length; i++) {
                if(strings[i].isEmpty()) {
                    historyFromString(strings[strings.length-1]);
                    break;
                }
                super.addTask(fromString((strings[i])));
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
