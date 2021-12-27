package allTasks;

import java.util.ArrayList;

public class EpicTask extends Task{
    protected ArrayList<SubTask> subTasks;

    public EpicTask(String name, String description) {
        super(name,description,Status.NEW);
        subTasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks.length=" + subTasks.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteSubtask(SubTask task) {
        subTasks.remove(task);
    }

    public void addSubtask(SubTask subTask) {
        subTasks.add(subTask);
        this.checkStatus();
    }

    public void checkStatus() {
        if(subTasks.size() == 0) {
            this.setStatus(Status.NEW);
            return;
        }
        int count = 0;
        for (SubTask subTask : subTasks) {
            if(subTask.getStatus() == Status.DONE) count++;
        }
        if(count == subTasks.size()) {
            this.setStatus(Status.DONE);
            return;
        }
        count = 0;
        for (SubTask subTask : subTasks) {
            if(subTask.getStatus() == Status.NEW) count++;
        }
        if(count == subTasks.size()) {
            this.setStatus(Status.NEW);
            return;
        }
        this.setStatus(Status.IN_PROGRESS);
    }

}
