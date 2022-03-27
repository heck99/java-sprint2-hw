package FunctionalTest;


import Functional.Assistance.HistoryManager;
import Functional.InMemory.InMemoryHistoryManager;
import allTasks.Status;
import allTasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static Helpers.TaskWorker.getTask;
import static Helpers.TaskWorker.compareTask;

//покрыт весь код за исключением метода toString
class HistoryManagerTest {
    public static HistoryManager manager;

    @BeforeEach
    private void createEmptyHistoryManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldBeEmptyWhenClearEmptyHistory() {
        manager.clear();
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void shouldBeEmptyWhenClearNotEmptyHistory() {
        manager.add(new Task("name", "description", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(10)));
        manager.clear();
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void shouldBeOneElementWhenAddOneElementInEmptyHistory() {
        Task AddedTask = getTask();
        Task taskTodd = getTask();
        manager.add(taskTodd);
        Assertions.assertEquals(manager.getHistory().size(), 1);
        if(manager.getHistory().size() == 1){
            Task returnedTask = manager.getHistory().get(0);
            compareTask(returnedTask, AddedTask);
        }
    }

    @Test
    public void shouldBeOneElementWhenAddOneElementInHistoryWithThisElement() {
        Task AddedTask = getTask();
        Task taskTodd = getTask();
        manager.add(taskTodd);
        manager.add(taskTodd);
        Assertions.assertEquals(manager.getHistory().size(), 1);
        if(manager.getHistory().size() == 1){
            Task returnedTask = manager.getHistory().get(0);
            compareTask(returnedTask, AddedTask);
        }
    }

    @Test
    public void shouldBeTwoElementWhenAddOneElementInHistoryWithOtherElement() {
        Task AddedTask = getTask();
        Task taskTodd = getTask();
        manager.add(taskTodd);
        Task secondTask = getTask();
        secondTask.setName("otherTask");
        secondTask.setId(2);
        Task secondAddedTask = getTask();
        secondAddedTask.setName("otherTask");
        secondAddedTask.setId(2);
        manager.add(secondTask);
        Assertions.assertEquals(manager.getHistory().size(), 2);
        if(manager.getHistory().size() == 2){
            Task returnedTask = manager.getHistory().get(0);
            compareTask(returnedTask, AddedTask);
            returnedTask = manager.getHistory().get(1);
            compareTask(returnedTask, secondAddedTask);
        }
    }

    @Test
    public void shouldBeEmptyWhenRemoveOneElementInHistoryWithOnlyThisElement() {
        Task task = getTask();
        manager.add(task);
        manager.remove(task.getId());
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void shouldBeEmptyWhenRemoveOneElementInHistoryWithThisElementFirst() {
        Task task = getTask();
        manager.add(task);
        Task secondTask = getTask();
        secondTask.setId(2);
        manager.add(secondTask);
        Task thirdTask = getTask();
        thirdTask.setId(3);
        manager.add(thirdTask);
        manager.remove(task.getId());
        Assertions.assertEquals(manager.getHistory().size(), 2);
    }

    @Test
    public void shouldBeEmptyWhenRemoveOneElementInHistoryWithThisElementLast() {
        Task task = getTask();
        manager.add(task);
        Task secondTask = getTask();
        secondTask.setId(2);
        manager.add(secondTask);
        Task thirdTask = getTask();
        thirdTask.setId(3);
        manager.add(thirdTask);
        manager.remove(thirdTask.getId());
        Assertions.assertEquals(manager.getHistory().size(), 2);
    }

    @Test
    public void shouldBeEmptyWhenRemoveOneElementInHistoryWithThisElementInMiddle() {
        Task task = getTask();
        manager.add(task);
        Task secondTask = getTask();
        secondTask.setId(2);
        manager.add(secondTask);
        Task thirdTask = getTask();
        thirdTask.setId(3);
        manager.add(thirdTask);
        manager.remove(secondTask.getId());
        Assertions.assertEquals(manager.getHistory().size(), 2);
    }

    @Test
    public void shouldBeOneElementWhenRemoveOneElementInHistoryWithTOtherElement() {
        Task task = getTask();
        Task otherTask = getTask();
        otherTask.setId(2);
        manager.add(otherTask);
        manager.remove(task.getId());
        Assertions.assertEquals(manager.getHistory().size(), 1);
    }

    @Test
    public void shouldBeZeroElementWhenRemoveOneElementInEmptyHistory() {
        manager.remove(0);
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void shouldReturnEmptyListWhenHistoryIsEmpty() {
        Assertions.assertEquals(manager.getHistory().size(), 0);
    }

    @Test
    public void shouldReturnListWithOneElementWhenHistoryNotEmpty() {
        manager.add(getTask());
        Assertions.assertEquals(manager.getHistory().size(), 1);
        if(manager.getHistory().size() == 1) {
            compareTask(manager.getHistory().get(0), getTask());
        }
    }


   // List<Task> getHistory();
}