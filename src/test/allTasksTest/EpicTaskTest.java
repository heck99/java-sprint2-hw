package allTasksTest;

import allTasks.EpicTask;
import allTasks.Status;
import allTasks.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class EpicTaskTest {

    public static EpicTask epic;

    @BeforeEach
    public  void createEpic(){
        epic = new EpicTask("epicTask", "description");
    }

    @Test
    public void shouldBeNewStatusWhenNoSubtasks() {
        epic.updateInfo();
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeNewStatusWhenAllSubtasksNewStatus() {
        SubTask sub1 = new SubTask("s1", "ds1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub2 = new SubTask("s2", "ds3", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub3 = new SubTask("s3", "ds1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        epic.updateInfo();
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldBeDoneStatusWhenAllSubtasksDoneStatus() {
        SubTask sub1 = new SubTask("s1", "ds1", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub2 = new SubTask("s2", "ds3", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub3 = new SubTask("s3", "ds1", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        epic.updateInfo();
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldBeIN_PROGRESSStatusWhenSubtasksDoneAndNewStatus() {
        SubTask sub1 = new SubTask("s1", "ds1", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub2 = new SubTask("s2", "ds3", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub3 = new SubTask("s3", "ds1", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub4 = new SubTask("s4", "ds4", Status.DONE, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        epic.updateInfo();
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldBeIN_PROGRESSStatusWhenAllSubtasksIn_progressStatus() {
        SubTask sub1 = new SubTask("s1", "ds1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub2 = new SubTask("s2", "ds3", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub3 = new SubTask("s3", "ds1", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        SubTask sub4 = new SubTask("s4", "ds4", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10L), epic);
        epic.updateInfo();
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}