package Exception;

public class TaskFindException extends RuntimeException{
    public TaskFindException() {
    }

    public TaskFindException(String message) {
        super(message);
    }
}