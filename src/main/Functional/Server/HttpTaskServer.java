package Functional.Server;

import Functional.Assistance.Managers;
import Functional.Assistance.Manager;
import JsonAddapters.*;
import allTasks.*;
import com.google.gson.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Exception.*;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static Manager manager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static  HttpServer httpServer;
    private static  Gson gson;


    public static void main(String[] args) throws IOException {
        manager = Managers.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskAdapter()). registerTypeAdapter(SubTask.class,
                new SubtaskAdapter()).registerTypeAdapter(EpicTask.class, new EpicAdapter()).create();
    }

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskAdapter()). registerTypeAdapter(SubTask.class,
                new SubtaskAdapter()).registerTypeAdapter(EpicTask.class, new EpicAdapter()).create();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            String response = null;
            String path = httpExchange.getRequestURI().getPath().substring(6);
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json");
            switch(method) {
                case "POST":
                    String body = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                    switch (path) {
                        case "/task":
                            response = getResponsePostTask(manager, body, Task.class);
                            break;
                        case "/epic":
                            response = getResponsePostTask(manager, body, EpicTask.class);
                            break;
                        case "/subtask":
                            response = getResponsePostTask(manager, body, SubTask.class);
                            break;
                    }
                    break;
                case "GET":
                    if(httpExchange.getRequestURI().getQuery() == null) {
                        switch (path) {
                            case "/task":
                            case "/epic":
                            case "/subtask":
                                response = getResponseGetTaskList(manager);
                                break;
                            case "/history":
                                response = getResponseGetHistory(manager);
                                break;
                            case "":
                                response = getResponseGetPrioritizedTasks(manager);
                                break;
                        }
                    } else {
                        int id = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring(3));
                        switch (path) {
                            case "/task":
                                response = getResponseGetTaskById(manager, id, Task.class);
                                break;
                            case "/epic":
                                response = getResponseGetTaskById(manager, id, EpicTask.class);
                                break;
                            case "/subtask":
                                response = getResponseGetTaskById(manager, id, SubTask.class);
                                break;
                            case "/subtask/epic":
                                response = getResponseGetEpicsSubtaskList(manager, id);
                                break;
                        }
                    }
                    break;
                case "DELETE":

                    if(httpExchange.getRequestURI().getQuery() == null) {
                        manager.deleteAllTask();
                        response = "?????? ???????????? ??????????????";
                    } else {
                        int id = Integer.parseInt(httpExchange.getRequestURI().getQuery().substring(3));
                        switch (path) {
                            case "/task" :
                                response = getResponseDeleteTaskById(manager, id, Task.class);
                                break;
                            case "/epic" :
                                response = getResponseDeleteTaskById(manager, id, EpicTask.class);
                                break;
                            case "/subtask" :
                                response = getResponseDeleteTaskById(manager, id, SubTask.class);
                                break;
                        }
                    }
                    break;
            }
            setStatus(httpExchange, response);
            try (OutputStream os = httpExchange.getResponseBody()) {
                if(response != null) {
                    os.write(response.getBytes());
                }
            }
        }

        private String getResponseGetTaskList(Manager manager) {
            List<Task> list = manager.getTaskList();
            return gson.toJson(list);
        }

        private void setStatus(HttpExchange httpExchange, String response) throws IOException {
            if(response != null) {
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private String getResponseGetTaskById(Manager manager, int id, Type type) {
            try {
                Task task = manager.getTaskById(id);
                if(task.getClass() != type) return null;
                return gson.toJson(task);
            } catch (TaskFindException exception) {
                return null;
            }
        }

        private String getResponseGetEpicsSubtaskList(Manager manager, int id) {
            try {
                Task task = manager.getTaskById(id);
                if(task.getClass() != EpicTask.class) return null;
                ArrayList<SubTask> list = manager.getEpicsSubtaskList(id);
                return gson.toJson(list);
            } catch (TaskFindException exception) {
                return null;
            }

        }

        private String getResponseGetHistory(Manager manager) {
            List<Task> list = manager.history();
            return gson.toJson(list);
        }

        private String getResponseGetPrioritizedTasks(Manager manager) {
            List<Task> list = manager.getPrioritizedTasks();
            return gson.toJson(list);
        }

        private String getResponseDeleteTaskById(Manager manager, int id, Type type) {
            try {
                Task task = manager.getTaskById(id);
                if(task.getClass() != type) return null;
                manager.deleteTask(id);
                //???????????? ???????????????? ????????????
                return gson.toJson(task);
            } catch (TaskFindException exception) {
                return null;
            }
        }

        private String getResponsePostTask(Manager manager, String body, Type type) {
            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if(jsonObject.keySet().contains("id")) {
                int id = jsonObject.get("id").getAsInt();
                if (type != manager.getTaskById(id).getClass()) return null;
                Task task = gson.fromJson(body, type);
                task.setId(id);
                manager.updateTask(task);
                //???????????? ???????????????????? ????????????
                return gson.toJson(manager.getTaskById(id));
            } else {
                if (Task.class.equals(type)) {
                    Task task = gson.fromJson(body, Task.class);
                    manager.addTask(task);
                    //???????????? ?????????????????????? ????????????
                    return gson.toJson(task);
                } else if(SubTask.class.equals(type)) {
                    SubTask task = gson.fromJson(body, SubTask.class);
                    task.setEpicTask(manager.getEpicById(task.getEpicId()));
                    manager.addTask(task);
                    //???????????? ?????????????????????? ????????????
                    return gson.toJson(task);
                } else if(EpicTask.class.equals(type)) {
                    EpicTask task = gson.fromJson(body, EpicTask.class);
                    manager.addTask(task);
                    //???????????? ?????????????????????? ????????????
                    return gson.toJson(task);
                }
            }
            return null;
        }
    }
}
