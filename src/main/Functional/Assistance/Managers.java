package Functional.Assistance;

import Functional.InMemory.InMemoryHistoryManager;
import Functional.InMemory.InMemoryTasksManager;
import Functional.Server.HTTPTaskManager;

public class Managers {
    public static InMemoryTasksManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
