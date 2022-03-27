import Functional.Assistance.Assistant;
import Functional.Assistance.Manager;
import Functional.InFile.FileBackedTasksManager;

import java.io.File;
import java.util.Scanner;

/*Проверял с помощью данного класса, в данный момент getDefault возвращает FileBackedTasksManager*/

public class Main {
    public static void main(String[] args) {
        System.out.println("Трекер задач запущен. Добро пожаловть! \nВыберете действие!");
        Scanner scanner = new Scanner(System.in);
        Manager manager = FileBackedTasksManager.loadFromFile(new File("base.csv"));
        while (true) {
            printMenu();
            int command = scanner.nextInt();
            System.out.println();
            switch (command) {
                case 1:
                    Assistant.printAllTaskList(manager.getTaskList());
                    break;
                case 2:
                    Assistant.printAllEpicTaskList(manager.getEpicTaskList());
                    break;
                case 3:
                    Assistant.printEpicsSubtasks(manager);
                    break;
                case 4:
                    Assistant.printOneTasks(manager);
                    break;
                case 5:
                    manager.addTask(Assistant.createTask(manager));
                    break;
                case 6:
                    Assistant.updateInfo(manager);
                    break;
                case 7:
                    Assistant.deleteOneElement(manager);
                    break;
                case 8:
                    manager.deleteAllTask();
                    break;
                case 9: Assistant.printHistory(manager);
                    break;
                case 10: Assistant.printAllTaskList(manager.getPrioritizedTasks());
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Данной команды не существует. Выберети команду из списка.");
                    break;
            }

        }
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("1.Получить список всех задач");
        System.out.println("2.Получить список всех эпиков");
        System.out.println("3.Получить список всех подзадач определённого эпика");
        System.out.println("4.Получить любую задачу");
        System.out.println("5.Добавить новую задачу");
        System.out.println("6.Обновить задачу");
        System.out.println("7.Удалить задачу");
        System.out.println("8.Удалить все задачи");
        System.out.println("9.Посмотреть историю");
        System.out.println("10.ВЫвести задачи в отсортированном виде");
        System.out.println("0.Выход");
    }
}
