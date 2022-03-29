package Functional.InFile;

import Functional.InMemory.InMemoryTasksManager;
import allTasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import Exception.*;


public class FileBackedTasksManager extends InMemoryTasksManager {
    private String path;

    public FileBackedTasksManager(String path) {
        super();
        this.path = path;
        loadFromFile();
    }

    public FileBackedTasksManager() {
        super();
    }


    protected void save() {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            bw.write(String.join(",","id", "type", "name", "status", "description","startTime", "duration", "epic"));
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
            throw new ManagerSaveException();
        }
    }

/*возможно, все методы, которые как либо получают задачу или историю из строки и наоборот нужно вынести в отдельный
 класс, но мне кажется, что это достаточно узкая задача, в том плане, что работает только для того формата,
 который задан в файле base.csv и вряд ли каким-либо другим классам понадобиться воспользоваться данными методами,
поэтому я решил сделать эти методы только для данного класса, потому что они нужны для работы только его самого*/

    private void historyFromString(String data) {
        String[] tasksId = data.split(",");
        for (String id : tasksId) {
            super.getTaskById(Integer.parseInt(id));
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file.getPath());
    }

    private void loadFromFile() {
        if (!Files.exists(Path.of(path))) return;
        try {
            String data = Files.readString(Path.of(path));
            String[] strings = data.split("\\r\\n");

            for (int i = 1; i < strings.length; i++) {
                if(strings[i].isEmpty()) {
                    historyFromString(strings[strings.length-1]);
                    break;
                }
                super.addTaskWithoutId(fromString((strings[i])));
            }

        } catch (IOException ex) {
            throw new ManagerSaveException();
        }
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        Task taskToReturn;
        switch (TaskType.valueOf(fields[1])) {
            case TASK:
                taskToReturn = new Task(fields[2], fields[4], Status.valueOf(fields[3]), LocalDateTime.parse(fields[5],
                        Task.getDATE_TIME_FORMATTER()), Duration.parse(fields[6]));
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            case EPIC:
                taskToReturn = new EpicTask(fields[2], fields[4]);
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            case SUBTASK:
                taskToReturn = new SubTask(fields[2], fields[4], Status.valueOf(fields[3]),  LocalDateTime.parse(fields[5],
                        Task.getDATE_TIME_FORMATTER()), Duration.parse(fields[6]), this.getEpicById(Integer.parseInt(fields[7])));
                taskToReturn.setId(Integer.parseInt(fields[0]));
                break;
            default:
                return null;
        }
        return taskToReturn;
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
