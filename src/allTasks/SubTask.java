package allTasks;

import allTasks.EpicTask;
import allTasks.Status;
import allTasks.Task;

import java.util.Scanner;

public class SubTask extends Task {
    protected EpicTask epicTask;

    public SubTask(String name, String description, Status status, EpicTask epicTask) {
        super(name, description, status);
        this.epicTask = epicTask;
    }

    public void setEpicTask(EpicTask epicTask) {
        this.epicTask = epicTask;
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTask=" + epicTask.getName() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public String toString(int command) {
        return String.join(",", String.valueOf(id), TaskType.TASK.toString(),
                name, status.toString(), description, String.valueOf(epicTask.getId()));
    }


}
