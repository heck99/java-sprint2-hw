package allTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH");

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public static Comparator<Task> getTimeComparator() {
        return (o1, o2) -> {
            LocalDateTime o1Time = o1.getStartTime();
            LocalDateTime o2Time = o2.getStartTime();
            if(o1Time.isAfter(o2Time)) return 1;
            if(o1Time.isBefore(o2Time)) return -1;
            return Integer.compare(o1.getId(), o2.getId());
        };
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }



    public static DateTimeFormatter getDATE_TIME_FORMATTER() {
        return DATE_TIME_FORMATTER;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime.format(DATE_TIME_FORMATTER) +
                '}';
    }

    //нужно ли что-то добавлять при перегрузке метода?
    public String toString(int command) {
        return String.join(",", String.valueOf(id), TaskType.TASK.toString(), name, status.toString(),
                description, startTime.format(DATE_TIME_FORMATTER), duration.toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
