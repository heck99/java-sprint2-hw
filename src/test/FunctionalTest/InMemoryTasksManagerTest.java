package FunctionalTest;

import Functional.InMemory.InMemoryTasksManager;
import Functional.Assistance.Manager;
import allTasks.EpicTask;
import allTasks.SubTask;
import allTasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Exception.*;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static Helpers.TaskWorker.*;

class InMemoryTasksManagerTest {

    Manager manager;

    @BeforeEach
    public void createManager() {
        manager  = new InMemoryTasksManager();
    }

    //void addTask(Task task);

    @Test
    public void shouldReturnOneAddedTaskWhenAddANewTask() {
        Task task = getTask();
        Task addedTask = getTask();
        manager.addTask(task);
        List<Task> returnedList = manager.getTaskList();
        assertEquals(returnedList.size(), 1);
        assertEquals(returnedList.get(0).getId(), 0);
        addedTask.setId(0);
        compareTask(returnedList.get(0), addedTask);
    }

    @Test
    public void shouldReturnTwoAddedSubTaskWhenAddANewSubTask() {
        EpicTask epic = getEpicTask();
        EpicTask addedEpic = getEpicTask();
        addedEpic.setId(0);
        manager.addTask(epic);
        SubTask addedSubtask = getSubTask(addedEpic);
        addedEpic.updateInfo();
        addedSubtask.setId(1);
        SubTask subTask = getSubTask(epic);
        manager.addTask(subTask);
        List<Task> returnedList = manager.getTaskList();
        assertEquals(returnedList.size(), 2);
        assertEquals(returnedList.get(0).getId(), 0);
        for (int i = 0; i < returnedList.size(); i++) {
            if (SubTask.class.equals(returnedList.get(i).getClass())) {
                compareSubTask((SubTask) returnedList.get(i), addedSubtask);
            } else if (EpicTask.class.equals(returnedList.get(i).getClass())) {
                compareEpicTask((EpicTask) returnedList.get(i), addedEpic);
            }
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddTaskWhenTheTimeIsReserved() {
        Task task = getTask();
        Task secondTask = getTask();
        manager.addTask(task);
        assertThrows(TaskAddException.class,() -> manager.addTask(secondTask));
    }

    @Test
    public void shouldReturnEmptyListWhenDeleteAllTasksFromEmptyManager() {
        manager.deleteAllTask();
        assertEquals(manager.getTaskList().size(), 0);
    }

    @Test
    public void shouldReturnEmptyListWhenDeleteAllTasksFromNotEmptyManager() {
        Task task = getTask();
        manager.addTask(task);
        manager.deleteAllTask();
        assertEquals(manager.getTaskList().size(), 0);
    }

    @Test
    public void shouldReturnEmptyTaskListWhenGetEmptyManager() {
        assertEquals(manager.getTaskList().size(), 0);
    }

    @Test
    public void shouldReturnNotEmptyTaskListWhenGetNotEmptyManager() {
        Task task = getTask();
        manager.addTask(task);
        Task addedTask = getTask();
        addedTask.setId(0);
        assertEquals(manager.getTaskList().size(), 1);
        if(manager.getTaskList().size() != 1) return;
        compareTask(manager.getTaskList().get(0), addedTask);
    }

    @Test
    public void shouldReturnEmptyEpicTaskListWhenGetEmptyManager() {
        assertEquals(manager.getEpicTaskList().size(), 0);
    }

    @Test
    public void shouldReturnNotEmptyEpicTaskListWhenGetNotEmptyManager() {
        EpicTask task = getEpicTask();
        task.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        manager.addTask(task);
        EpicTask addedTask = getEpicTask();
        addedTask.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        addedTask.setId(0);
        assertEquals(manager.getTaskList().size(), 1);
        if(manager.getTaskList().size() != 1) return;
        compareEpicTask(manager.getEpicTaskList().get(0), addedTask);
    }

    @Test
    public void shouldReturnEmptyEpicTaskListWhenGetManagerWithNotEpicTask() {
        Task task = getTask();
        manager.addTask(task);
        assertEquals(manager.getEpicTaskList().size(), 0);
    }

    @Test
    public void shouldReturnEpicTaskListWhenGetNotEmptyManagerWithOthersTasks() {
        Task task = getTask();
        manager.addTask(task);
        EpicTask epicTask = getEpicTask();
        epicTask.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        manager.addTask(epicTask);
        EpicTask addedEpicTask = getEpicTask();
        addedEpicTask.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        addedEpicTask.setId(1);
        assertEquals(manager.getEpicTaskList().size(), 1);
        if(manager.getEpicTaskList().size() != 1) return;
        compareEpicTask( manager.getEpicTaskList().get(0), addedEpicTask);
    }

    @Test
    public void shouldReturnEmptySubTaskListWhenEpicTaskHaveNoSubTasks() {
        EpicTask task = getEpicTask();
        manager.addTask(task);
        assertEquals(manager.getEpicsSubtaskList(task.getId()).size(), 0);
    }

    @Test
    public void shouldReturnNotEmptySubTaskListWhenEpicTaskHaveSubTasks() {
        EpicTask task = getEpicTask();
        SubTask subTask = getSubTask(task);
        manager.addTask(task);
        manager.addTask(subTask);
        List<SubTask> list = manager.getEpicsSubtaskList(task.getId());
        assertEquals(list.size(), 1);
        SubTask addedSubTask = getSubTask(task);
        addedSubTask.setId(1);
        compareSubTask(list.get(0), addedSubTask);
    }

    @Test
    public void shouldThrowExceptionWhenTaskWithIdNotExistWhenGetEpicsSubtaskList() {
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getEpicsSubtaskList(2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenTaskWithIdOtherType() {
        Task task = getTask();
        manager.addTask(task);
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getEpicsSubtaskList(0));
        assertEquals("Данная задача не является эпиком", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenTaskWithIdNotExist() {
        Task task = getTask();
        manager.addTask(task);
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getTaskById(2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenManagerIsEmpty() {
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getTaskById(2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldReturnTaskById() {
        Task task = getTask();
        manager.addTask(task);
        Task addedTask = getTask();
        addedTask.setId(0);
        Task returnedTask = manager.getTaskById(task.getId());
        compareTask(addedTask, returnedTask);
    }

    @Test
    public void shouldAddElementToHistoryList() {
        Task task = getTask();
        manager.addTask(task);
        Task addedTask = getTask();
        addedTask.setId(0);
        manager.getTaskById(task.getId());
        List<Task> list = manager.history();
        assertEquals(list.size(), 1);
        if(list.size() != 1) return;
        compareTask(list.get(0), addedTask);
    }

    @Test
    public void shouldReplaceElementInHistoryList() {
        Task task = getTask();
        manager.addTask(task);
        Task addedTask = getTask();
        addedTask.setId(0);
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());
        List<Task> list = manager.history();
        assertEquals(list.size(), 1);
        if(list.size() != 1) return;
        compareTask(list.get(0), addedTask);
    }

    @Test
    public void shouldUpdateTask() {
        Task task = getTask();
        manager.addTask(task);
        Task newTask = getTask();
        newTask.setName("update");
        Task addedTask = getTask();
        addedTask.setName("update");
        addedTask.setId(0);
        manager.updateTask(newTask);
        List<Task> list = manager.getTaskList();
        assertEquals(list.size(), 1);
        if(list.size() != 1) return;
        compareTask(list.get(0), addedTask);
    }

    @Test
    public void shouldThrowExceptionWhenNoTaskWithId() {
        Task task = getTask();
        manager.addTask(task);
        Task task2 = getTask();
        task2.setId(3);
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.updateTask(task2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenTimeIsBuzy() {
        Task task = getTask();
        task.setStartTime(LocalDateTime.of(2020,3,12,12,12,12));
        manager.addTask(task);
        Task task2 = getTask();
        manager.addTask(task2);
        Task task3 =getTask();
        task3.setName("otherName");
        assertThrows(TaskAddException.class,() -> manager.updateTask(task3));
    }

    @Test
    public void shouldThrowExceptionWhenUpdateTaskAndManagerIsEmpty() {
        Task task = getTask();
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.updateTask(task));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenDeleteTaskAndManagerIsEmpty() {
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.deleteTask(1));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenDeleteTaskWithIdDoNotExist() {
        Task task = getTask();
        manager.addTask(task);
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.deleteTask(1));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldDeleteTask() {
        Task task = getTask();
        manager.addTask(task);
        manager.deleteTask(task.getId());
        assertEquals(manager.getTaskList().size(), 0);
    }

    @Test
    public void shouldDeleteEpicAndAllSubTasks() {
        EpicTask epic = getEpicTask();
        manager.addTask(epic);
        SubTask sub = getSubTask(epic);
        SubTask sub2 = getSubTask(epic);
        sub2.setStartTime(LocalDateTime.of(2022,4,20,12,12,12));
        manager.addTask(sub);
        manager.addTask(sub2);
        assertEquals(manager.getTaskList().size(), 3);
        manager.deleteTask(epic.getId());
        assertEquals(manager.getTaskList().size(), 0);
    }

    @Test
    public void shouldDeleteSubTaskFromManagerAndEpicTasks() {
        EpicTask epic = getEpicTask();
        manager.addTask(epic);
        SubTask sub = getSubTask(epic);
        manager.addTask(sub);
        assertEquals(manager.getTaskList().size(), 2);
        assertEquals(manager.getEpicsSubtaskList(epic.getId()).size(), 1);
        manager.deleteTask(sub.getId());
        assertEquals(manager.getTaskList().size(), 1);
        assertEquals(manager.getEpicsSubtaskList(epic.getId()).size(), 0);
    }

    @Test
    public void shouldThrowExceptionWhenEpicTaskWithIdNotExist() {
        EpicTask task = getEpicTask();
        manager.addTask(task);
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getEpicById(2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenGetEpicTaskAndManagerIsEmpty() {
        final TaskFindException exception = assertThrows(TaskFindException.class,() -> manager.getEpicById(2));
        assertEquals("Нет задачи с данным id", exception.getMessage());
    }

    @Test
    public void shouldReturnEpicTaskById() {
        EpicTask task = getEpicTask();
        task.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        manager.addTask(task);
        EpicTask addedEpicTask = getEpicTask();
        addedEpicTask.setStartTime(LocalDateTime.of(2022,3,14,0,50,0));
        Task returnedTask = manager.getEpicById(task.getId());
        compareTask(addedEpicTask, returnedTask);
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(manager.history().size(), 0);
    }

    @Test
    public void shouldReturnNotEmptyHistory() {
        Task task = getTask();
        Task addedTask = getTask();
        addedTask.setId(0);
        manager.addTask(task);
        manager.getTaskById(task.getId());
        List<Task> list = manager.history();
        assertEquals(list.size(), 1);
        if(list.size() != 1) return;
        compareTask(list.get(0), addedTask);
    }

    @Test
    public void shouldReturnEmptyPrioritizedList() {
        assertEquals(manager.getPrioritizedTasks().size(), 0);
    }

    @Test
    public void shouldReturnNotEmptyPrioritizedList() {
        Task task = getTask();
        Task addedTask = getTask();
        addedTask.setId(0);
        manager.addTask(task);
        List<Task> list = manager.getPrioritizedTasks();
        assertEquals(list.size(), 1);
        if(list.size() != 1) return;
        compareTask(list.get(0), addedTask);
    }









}