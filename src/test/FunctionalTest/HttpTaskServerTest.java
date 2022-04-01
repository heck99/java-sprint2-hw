package FunctionalTest;

import Functional.Server.HttpTaskServer;
import Functional.Server.KVServer;
import JsonAddapters.*;
import allTasks.EpicTask;
import allTasks.Status;
import allTasks.SubTask;
import allTasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class HttpTaskServerTest {

    KVServer KVserver;
    HttpTaskServer taskServer;
    static Gson gson;

    @BeforeAll
    public static void setGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(EpicTask.class, new EpicAdapter());
        gson = gsonBuilder.create();
    }

    @BeforeEach
    public void start() throws IOException {
        KVserver = new KVServer();
        KVserver.start();
        taskServer = new HttpTaskServer();

    }

    @AfterEach
    public void stop() {
        KVserver.stop();
        taskServer.stop();
    }

    public static Task getTask() {
        return new Task("task", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 3, 9, 17, 0), Duration.ofMinutes(60));
    }

    public static EpicTask getEpicTask() {
        return new EpicTask("epic", "description");
    }

    public static SubTask getSubTask(EpicTask epic) {
        return new SubTask("sub", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2022, 3, 12, 22, 0), Duration.ofMinutes(60), epic);
    }

    @Test
    public void shouldReturn404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/tas");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn1TaskWhenManagerAddTask() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

    @Test
    public void shouldReturn1TaskWhenManagerUpdateTask() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":0,\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
    }

    @Test
    public void shouldReturn1TaskWhenManagerAddEpic() throws IOException, InterruptedException {
        Task task = getEpicTask();
        task.setId(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

    @Test
    public void shouldReturn1TaskWhenManagerUpdateEpic() throws IOException, InterruptedException {
        EpicTask task = getEpicTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description2\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":0,\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

    @Test
    public void shouldReturn1TaskWhenManagerAddSubtask() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(sub));
    }

    @Test
    public void shouldReturn1TaskWhenManagerUpdateSubtask() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":50,\"startTime\":\"12.03.2022_21\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"id\":1,\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(sub));
    }

    @Test
    public void shouldReturnAllTaskWhenGetAndEndpoittask() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        Task task = getTask();
        task.setId(2);
        epic.updateInfo();
        ArrayList<Task> list = new ArrayList<>();
        list.add(epic);
        list.add(sub);
        list.add(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
         client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
        System.out.println(response.body());
    }

    @Test
    public void shouldReturnAllTaskWhenGetAndEndpoitepic() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        Task task = getTask();
        task.setId(2);
        epic.updateInfo();
        ArrayList<Task> list = new ArrayList<>();
        list.add(epic);
        list.add(sub);
        list.add(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
        System.out.println(response.body());
    }

    @Test
    public void shouldReturnAllTaskWhenGetAndEndpoitsubtask() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        Task task = getTask();
        task.setId(2);
        epic.updateInfo();
        ArrayList<Task> list = new ArrayList<>();
        list.add(epic);
        list.add(sub);
        list.add(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpRequest request4 = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
        System.out.println(response.body());
    }

    @Test
    public void shouldReturn1TaskWhenGetTaskById() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/task?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

    @Test
    public void shouldReturn404WhenGetTaskNoId() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/task?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn404WhenGetOtherEndpoint() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/epic?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn1TaskWhenGetEpicById() throws IOException, InterruptedException {
        EpicTask task = getEpicTask();
        task.setId(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/epic?id=0");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

    @Test
    public void shouldReturn404WhenGetEpicOtherEndpoint() throws IOException, InterruptedException {
        EpicTask task = getEpicTask();
        task.setId(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/task?id=0");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn404WhenGetEpicNoId() throws IOException, InterruptedException {
        EpicTask task = getEpicTask();
        task.setId(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/epic?id=2");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"epic\",\"description\":\"description\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn1TaskWhenGetSubtaskById() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(sub));
    }

    @Test
    public void shouldReturn404WhenGetSubtaskNoId() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturn404WhenGetSubtaskOtherEndpoint() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturnSubtaskWhenGetEpicsSubtasks() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        List<SubTask> list = new ArrayList<>();
        list.add(sub);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/epic?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
    }

    @Test
    public void shouldReturnEmptyBodyListWhenGetEpicsSubtasks() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        List<SubTask> list = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/epic?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
    }

    @Test
    public void shouldReturn404WhenGetEpicsSubtasksNoId() throws IOException, InterruptedException {
        EpicTask epic = getEpicTask();
        epic.setId(0);
        SubTask sub = getSubTask(epic);
        sub.setId(1);
        List<SubTask> list = new ArrayList<>();
        list.add(sub);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/epic?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"other\",\"description\":\"description1\"}"))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"sub\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"12.03.2022_22\",\"epicId\":0}"))
                .build();
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 404);
    }

    @Test
    public void shouldReturnFirstTaskBeforeSecond() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        Task second = getTask();
        second.setStartTime(LocalDateTime.of(2022, 3, 9 , 18, 0));
        second.setId(1);
        List<Task> list = new LinkedList<>();
        list.add(task);
        list.add(second);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_18\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
    }

    @Test
    public void shouldReturnSecondBeforeFirst() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        Task second = getTask();
        second.setStartTime(LocalDateTime.of(2022, 3, 9 , 16, 0));
        second.setId(1);
        List<Task> list = new LinkedList<>();
        list.add(second);
        list.add(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_16\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
    }

    @Test
    public void shouldReturnHistory() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        List<Task> list = new ArrayList<>();
        list.add(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/task?id=0");
        URI url3 = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(list));
    }

    @Test
    public void shouldDeleteHistoryWhenEndpointtask() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи удалены");
    }

    @Test
    public void shouldDeleteHistoryWhenEndpointepic() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url2)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи удалены");
    }

    @Test
    public void shouldDeleteHistoryWhenEndpointsubtask() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), "Все задачи удалены");
    }

    @Test
    public void shouldReturnDeletedTask() throws IOException, InterruptedException {
        Task task = getTask();
        task.setId(0);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        URI url2 = URI.create("http://localhost:8080/tasks/task?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"task\",\"description\":\"description\"," +
                        "\"status\":\"IN_PROGRESS\",\"duration\":60,\"startTime\":\"09.03.2022_17\"}"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(response.statusCode(), 200);
        Assertions.assertEquals(response.body(), gson.toJson(task));
    }

}
