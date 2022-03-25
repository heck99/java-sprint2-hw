package Functional;

import allTasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*Как я понял, этот класс не требуется для Тз. Он служит только для взаимодействия пользователя
с классом менеджер и для удобной отладки */

public class Assistant {

    private static void printEpicTaskList(ArrayList<EpicTask> taskList) {
        for(EpicTask task : taskList) {
            System.out.println("id - " + task.getId() + " name - "+task.getName());
        }
    }

    private static void printTaskList(ArrayList<Task> taskList) {
        for(Task task : taskList) {
            System.out.println("id - " + task.getId() + " name - " + task.getName());
        }
    }

    public static void printEpicsSubtasks(Manager manager) {
        System.out.println("Введите id Эпика");
        Scanner scanner = new Scanner(System.in);
        printEpicTaskList(manager.getEpicTaskList());

        int id=scanner.nextInt();
        scanner.nextLine();

        if(manager.getEpicsSubtaskList(id) == null) {
            System.out.println("Эпика с данным id не существует");
            return;
        }
        printSubtaskList(manager.getEpicsSubtaskList(id));
    }

    public static void printOneTasks(Manager manager) {
        System.out.println("Введите id задачи");
        printTaskList(manager.getTaskList());
        Scanner scanner = new Scanner(System.in);
        Task task = manager.getTaskById(scanner.nextInt());
        scanner.nextLine();
        while(task == null) {
            System.out.println("Такого id не существует или id неверный\nВведите корректный id");
            task = manager.getTaskById(scanner.nextInt());
            scanner.nextLine();
        }
        System.out.println(task);
    }

    private static Task createCommonTask() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя задачи");
        String name = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description=scanner.nextLine();
        System.out.println("выберите статус задачи");
        Status.printAllStatus();
        int command;
        do {
            command = scanner.nextInt();
            scanner.nextLine();
        }while (command > 3 || command <= 0);
        Status status=Status.values()[command - 1];
        return new Task(name, description, status, inputStartDate(), inputDuration());
    }

    private static EpicTask createEpicTask() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя задачи");
        String name = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description = scanner.nextLine();
        return new EpicTask(name, description);
    }

    public static void printHistory(Manager manager) {
        List<Task> history = manager.history();
        printAllTaskList(history);
    }

    /* Мне кажется, не совсем логично передавать manager как параметр, но по-другому я не придумал, как реализовать
    выбор эпика, поэтому я считаю, что это решения данной проблемы*/

    private static SubTask createSubtask(Manager manager) {
        System.out.println("Выберите id эпика задачи");
        Scanner scanner = new Scanner(System.in);
        printEpicTaskList(manager.getEpicTaskList());
        EpicTask epicTask=manager.getEpicById(scanner.nextInt());
        scanner.nextLine();
        while(epicTask == null || epicTask.getClass()!=EpicTask.class) {
            System.out.println("Такого id не существует или id неверный\nВведите корректный id");
            epicTask = manager.getEpicById(scanner.nextInt());
            scanner.nextLine();
        }
        System.out.println("Введите имя задачи");
        String name=scanner.nextLine();
        System.out.println("Введите описание задачи");
        String description=scanner.nextLine();
        System.out.println("выберите статус задачи");
        Status.printAllStatus();
        int command;
        do {
            command = scanner.nextInt();
            scanner.nextLine();
        }while (command > 3 || command <= 0);
        Status status = Status.values()[command - 1];
        return new SubTask(name, description, status, inputStartDate(), inputDuration(), epicTask);
    }

    private static LocalDateTime inputStartDate() {
        Scanner scanner =  new Scanner(System.in);
        System.out.println("Введите дату начала задачи в формате dd.mm.yyyy.hh");
        return LocalDateTime.parse(scanner.nextLine(),  DateTimeFormatter.ofPattern("dd.MM.yyyy.HH"));
    }

    private static Duration inputDuration() {
        Scanner scanner =  new Scanner(System.in);
        System.out.println("Введите длительность задачи в минутах");
        return Duration.ofMinutes(scanner.nextInt());
    }

    public static Task createTask(Manager manager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("какую задачу Вы хотите создать?");
        System.out.println("1.Обычную");
        System.out.println("2.Эпик ");
        System.out.println("3.Подзадачу");
        switch (scanner.nextInt()) {
            case 1:
                return createCommonTask();
            case 2:
                return createEpicTask();
            case 3:
                return createSubtask(manager);
        }
        return null;
    }

    private static void printSubtaskList(ArrayList<SubTask> taskList) {
        for(SubTask task : taskList) {
            System.out.println(task);
        }
    }

    public static void deleteOneElement(Manager manager) {
        System.out.println("Введите id задачи");
        printTaskList(manager.getTaskList());
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        scanner.nextLine();
        manager.deleteTask(id);
    }

    public static void updateInfo(Manager manager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите id задачи для обновления");
        printTaskList(manager.getTaskList());
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите новое имя, если имя не нужно менять оставьте поле пустым");
        String newName = scanner.nextLine();
        System.out.println("Введите новое описание, если имя не нужно менять оставьте поле пустым");
        String newDescription = scanner.nextLine();
        System.out.println("Выберите новый статус");
        Status.printAllStatus();
        int newStatusid = scanner.nextInt();
        Status newStatus = Status.values()[newStatusid-1];
        scanner.nextLine();
        Task taskToUpdate = new Task(newName, newDescription, newStatus, inputStartDate(), inputDuration());
        taskToUpdate.setId(id);
        manager.updateTask(taskToUpdate);


    }

    public static void printAllTaskList(List<Task> taskList) {
        for(Task task : taskList) {
            System.out.println(task);
        }
    }

    public static void printAllEpicTaskList(ArrayList<EpicTask> taskList) {
        for(EpicTask task : taskList) {
            System.out.println(task);
        }
    }
}
