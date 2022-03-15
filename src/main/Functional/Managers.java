package Functional;

public class Managers {
    public static InMemoryTasksManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager  getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
