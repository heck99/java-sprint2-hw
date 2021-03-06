package allTasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    protected EpicTask epicTask;
    private int epicId;

    public SubTask(String name, String description, Status status, LocalDateTime startTime, Duration duration, EpicTask epicTask) {
        super(name, description, status, startTime, duration);
        this.epicTask = epicTask;
        epicTask.addSubtask(this);
    }

    public SubTask(String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public void setEpicTask(EpicTask epicTask) {
        this.epicTask = epicTask;
        epicTask.addSubtask(this);
    }

    public int getEpicId() {
        return epicId;
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTask=" + epicTask.getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime.format(DATE_TIME_FORMATTER) +
                '}';
    }

    public String toString(int command) {
        return String.join(",", String.valueOf(id), TaskType.SUBTASK.toString(), name, status.toString(),
                description,  startTime.format(DATE_TIME_FORMATTER), duration.toString(), String.valueOf(epicTask.getId()));
    }
}
