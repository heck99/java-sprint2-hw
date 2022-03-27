package Helpers;

import allTasks.EpicTask;
import allTasks.Status;
import allTasks.SubTask;
import allTasks.Task;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskWorker {

    public static Task getTask() {
        return new Task("name", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 3, 12, 22, 24), Duration.ofMinutes(10));
    }

    public static EpicTask getEpicTask() {
        return new EpicTask("name", "description");
    }

    public static SubTask getSubTask(EpicTask epic) {
        return new SubTask("name", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 3, 12, 22, 24), Duration.ofMinutes(10), epic);
    }

    public static void compareTask(Task one, Task two) {
        Assertions.assertEquals(one.getId(), two.getId());
        Assertions.assertEquals(one.getStartTime(), two.getStartTime());
        Assertions.assertEquals(one.getDuration(), two.getDuration());
        Assertions.assertEquals(one.getName(), two.getName());
        Assertions.assertEquals(one.getDescription(), two.getDescription());
        Assertions.assertEquals(one.getStatus(), two.getStatus());
    }

    public static void compareEpicTask(EpicTask one, EpicTask two) {
        compareTask(one, two);
        List<SubTask> listOne = one.getSubTasks();
        List<SubTask> listTwo = two.getSubTasks();
        Assertions.assertEquals(listOne.size(), listTwo.size());
        if(listTwo.size() != listOne.size()) return;
        for (int i = 0; i < listOne.size(); i++) {
            compareTask(listOne.get(i), listTwo.get(i));
        }
    }

    public static void compareSubTask(SubTask one, SubTask two) {
        compareTask(one, two);
        compareEpicTask(one.getEpicTask(), one.getEpicTask());
    }
}
