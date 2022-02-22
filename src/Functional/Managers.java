package Functional;

public class Managers {
    public static InMemoryTasksManager getDefault() {
        return new FileBackedTasksManager("base.csv");
    }
}
