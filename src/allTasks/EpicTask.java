package allTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends Task{
    protected ArrayList<SubTask> subTasks;



    public EpicTask(String name, String description ) {
        //не понятно из ТЗ, что делать с временем эпика без подзадач
        super(name, description,  Status.NEW,  LocalDateTime.now(), Duration.ofMinutes(0));
        subTasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks=" + subTasks.size() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime.format(DATE_TIME_FORMATTER) +
                '}';
    }

    public String toString(int command) {
        return String.join(",", String.valueOf(id), TaskType.EPIC.toString(),
                name, status.toString(), description);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteSubtask(SubTask task) {
        subTasks.remove(task);
    }

    public void addSubtask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void updateInfo() {
        checkStatus();
        checkTime();
    }

    private void checkTime() {
        LocalDateTime startTime;
        Duration duration = Duration.ofMinutes(0);
        if(subTasks.size() == 0) {
            this.duration = duration;
            this.startTime = LocalDateTime.now();
            return;
        }
        startTime = subTasks.get(0).getStartTime();
        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime().isBefore(startTime)) {
                startTime = subTask.getStartTime();
            }
            duration = duration.plus(subTask.getDuration());
        }
        this.setDuration(duration);
        this.setStartTime(startTime);
    }


    private void checkStatus() {
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
