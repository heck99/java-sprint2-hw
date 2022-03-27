package Functional.Assistance;

import Functional.InMemory.InMemoryHistoryManager;
import Functional.InMemory.InMemoryTasksManager;

public class Managers {
    public static InMemoryTasksManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
