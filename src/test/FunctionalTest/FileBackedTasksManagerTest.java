package FunctionalTest;

import Functional.InFile.FileBackedTasksManager;
import Functional.Assistance.Manager;
import allTasks.EpicTask;
import allTasks.Status;
import allTasks.SubTask;
import allTasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static Helpers.TaskWorker.*;

class FileBackedTasksManagerTest {
    Manager manager;
    String emptyBasePath = "Emptybase.csv";
    String testBasePath = "test.csv";
    @BeforeEach
    public void createManager() {
        try {
            Files.copy(Path.of(emptyBasePath), Path.of(testBasePath));
        } catch(IOException ex) {
            assertEquals("файл существует", "фвйл не существует");
        }
        manager = new FileBackedTasksManager("test.csv");
    }

    @AfterEach
    public void DeleteFile() {
        try {
            Files.delete(Path.of(testBasePath));
        } catch(IOException ex) {
            assertEquals("файл существует", "фвйл не существует");
        }
    }
    //вся функциональность не связанная с записью и чтением файла была проверена в класск InMemoryTasksManagerTest
    //каждый переопределённый метод этого класса сначала вызывает функционал протестированного класса, а потом метод save
    //поэтому необходимо протестировать корректную работу метода save без задач/со всеми типами задач/без истории/с историей
    //для этого будем использовать переопределённый метод add
    //также проверим, что каждый переопределённый метод вызывает метод save

    @Test
    public void shouldAddOneTaskInFile() {
        Task task = getTask();
        manager.addTask(task);
        String expectedLine = getHead() + task.toString(1) + "\r\n\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldAddTwoTaskInFile() {
        Task task = getTask();
        manager.addTask(task);
        Task task2 = getTask();
        task2.setStartTime(LocalDateTime.of( 2022,3,12,12,12,0));
        manager.addTask(task2);
        String expectedLine = getHead() + task.toString(1) + "\r\n" + task2.toString(1) + "\r\n\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldBeFileWithNoTasks() {
        Task task = getTask();
        manager.addTask(task);
        manager.deleteTask(task.getId());
        String expectedLine = getHead()  + "\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldAddEpicTaskInFile() {
        EpicTask task = getEpicTask();
        manager.addTask(task);
        String expectedLine = getHead() + task.toString(1) + "\r\n" + "\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldAddEpicTaskAndSubTaskInFile() {
        EpicTask task = getEpicTask();
        SubTask sub = getSubTask(task);
        manager.addTask(task);
        manager.addTask(sub);
        String expectedLine = getHead() + task.toString(1) + "\r\n" + sub.toString(1) + "\r\n\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldAddHistoryInFile() {
        Task task = getTask();
        manager.addTask(task);
        manager.getTaskById(task.getId());
        String expectedLine = getHead() + task.toString(1) + "\r\n" + "\r\n" + task.getId();
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldAddUpdatedTaskInFile() {
        Task task = getTask();
        manager.addTask(task);
        Task other = getTask();
        other.setName("other");
        other.setId(0);
        manager.updateTask(other);
        String expectedLine = getHead() + other.toString(1) + "\r\n" + "\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldBeFileWithNoTasksAfterDeleteAll() {
        Task task = getTask();
        manager.addTask(task);
        manager.deleteAllTask();
        String expectedLine = getHead()  + "\r\n";
        assertEquals(expectedLine, getLines());
    }

    @Test
    public void shouldLoadTaskEpicSubTaskNoHistoryFromFile() {
        manager = FileBackedTasksManager.loadFromFile(new File("test1.csv"));

        Task task = new Task("t1", "dt1", Status.IN_PROGRESS,LocalDateTime.
                parse("07.03.2022_14", Task.getDATE_TIME_FORMATTER()), Duration.parse("PT1H"));
        task.setId(0);

        EpicTask epic = new EpicTask("э1", "de1");
        epic.setId(1);
        SubTask subTask = new SubTask("s1", "ds1", Status.IN_PROGRESS, LocalDateTime.
                parse("07.03.2022_20",Task.getDATE_TIME_FORMATTER()), Duration.parse("PT6H40M"),epic);
        subTask.setId(2);
        epic.updateInfo();
        List<Task> list = manager.getTaskList();
        for (Task task1 : list) {
            if (SubTask.class.equals(task1.getClass())) {
                compareSubTask((SubTask) task1, subTask);
            } else if (EpicTask.class.equals(task1.getClass())) {
                compareEpicTask((EpicTask) task1, epic);
            } else if (Task.class.equals(task1.getClass())) {
                compareTask(task1, task);
            }
        }
    }

    @Test
    public void shouldLoadHistoryFromFile() {
        manager = FileBackedTasksManager.loadFromFile(new File("test2.csv"));
        Task task = new Task("t1", "dt1", Status.IN_PROGRESS,LocalDateTime.
                parse("07.03.2022_14", Task.getDATE_TIME_FORMATTER()), Duration.parse("PT1H"));
        task.setId(0);
        EpicTask epic = new EpicTask("э1", "de1");
        epic.setId(1);
        SubTask subTask = new SubTask("s1", "ds1", Status.IN_PROGRESS, LocalDateTime.
                parse("07.03.2022_20",Task.getDATE_TIME_FORMATTER()), Duration.parse("PT6H40M"),epic);
        subTask.setId(2);
        epic.updateInfo();

        List<Task> list =manager.history();
        for (Task task1 : list) {
            if (SubTask.class.equals(task1.getClass())) {
                compareSubTask((SubTask) task1, subTask);
            } else if (EpicTask.class.equals(task1.getClass())) {
                compareEpicTask((EpicTask) task1, epic);
            } else if (Task.class.equals(task1.getClass())) {
                compareTask(task1, task);
            }
        }
    }



    private String getHead() {
        return "id,type,name,status,description,startTime,duration,epic\r\n";
    }

    private String getLines() {
        if (!Files.exists(Path.of(testBasePath))) return "";
        try {
            return Files.readString(Path.of(testBasePath));
        } catch (IOException ex) {
            assertEquals("Файл существует" ,"Файла не существут");
            return "";
        }
    }

}