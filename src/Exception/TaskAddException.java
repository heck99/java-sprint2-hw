package Exception;

public class TaskAddException extends RuntimeException{
    public TaskAddException() {
    }

    public TaskAddException(String message) {
        super(message);
    }
}
