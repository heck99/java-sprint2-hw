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


    public static void main(String[] args) throws IOException {
        manager = Managers.getDefault();
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
    }

    private static class TaskHandler implements HttpHandler {
        private static List<String> types = Arrays.asList("epic", "task", "subtask");


        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String method = httpExchange.getRequestMethod();
            String response = null;
            String path = httpExchange.getRequestURI().getPath();
            String[] fields = path.split("/");
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "application/json");
            switch(method) {
                case "POST":

                    response = getResponsePostTask(manager, new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET), getType(fields[2]));
                    setStatus(httpExchange, response);
                    break;
                case "GET":
                    //запрос на получение списка всех задач
                    if(fields.length == 3 && httpExchange.getRequestURI().getQuery() == null && types.contains(fields[2])) {
                        response = getResponseGetTaskList(manager);
                        setStatus(httpExchange, response);
                    }
                    //запрос на получение задачи по id сразу 3 endpoint для всех типов завач
                    if(fields.length == 3 && httpExchange.getRequestURI().getQuery() != null && types.contains(fields[2])) {
                        String id = httpExchange.getRequestURI().getQuery();
                        response = getResponseGetTaskById(manager, Integer.parseInt(id.split("=")[1]), getType(fields[2]));
                        setStatus(httpExchange, response);
                    }
                    //запрос на получение сабтаски эпика по id эпика
                    if(fields.length == 4 && httpExchange.getRequestURI().getQuery() != null && types.contains(fields[2])) {
                        String id = httpExchange.getRequestURI().getQuery();
                        if(!fields[2].equals(types.get(2))) break;
                        if (!fields[3].equals(types.get(0)) ) break;
                        response = getResponseGetEpicsSubtaskList(manager, Integer.parseInt(id.split("=")[1]));
                        setStatus(httpExchange, response);
                    }
                    //запрос на получение истории
                    if(fields.length == 3 && fields[2].equals("history")) {
                        response = getResponseGetHistory(manager);
                        setStatus(httpExchange, response);
                    }
                    //запрос на получение истории
                    if(fields.length == 2) {
                        response = getResponseGetPrioritizedTasks(manager);
                        setStatus(httpExchange, response);
                    }
                    break;

                case "DELETE":
                    //запрос на удаление всех задач
                    if(fields.length == 3 && httpExchange.getRequestURI().getQuery() == null && types.contains(fields[2])) {
                        manager.deleteAllTask();
                        response = "Все задачи удалены";
                        setStatus(httpExchange, response);
                    }
                    System.out.println("тут");
                    //запрос на удаление задачи по id
                    if(fields.length == 3 && httpExchange.getRequestURI().getQuery() != null && types.contains(fields[2])) {
                        String id = httpExchange.getRequestURI().getQuery();
                        response = getResponseDeleteTaskById(manager, Integer.parseInt(id.split("=")[1]), getType(fields[2]));
                        setStatus(httpExchange, response);
                    }

                    break;
                default:
                    setStatus(httpExchange, null);
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                if(response != null) {
                    os.write(response.getBytes());
                }
            }
        }

        private String getResponseGetTaskList(Manager manager) {
            List<Task> list = manager.getTaskList();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
            Gson gson = gsonBuilder.create();
            return gson.toJson(list);
        }

        private void setStatus(HttpExchange httpExchange, String response) throws IOException {
            if(response != null) {
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                httpExchange.sendResponseHeaders(403, 0);
            }
        }

        private String getResponseGetTaskById(Manager manager, int id, Type type) {
            try {
                Task task = manager.getTaskById(id);
                if(task.getClass() != type) return null;
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
                gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
                Gson gson = gsonBuilder.create();
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
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
                gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
                Gson gson = gsonBuilder.create();
                return gson.toJson(list);
            } catch (TaskFindException exception) {
                return null;
            }

        }

        private String getResponseGetHistory(Manager manager) {
            List<Task> list = manager.history();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
            Gson gson = gsonBuilder.create();
            return gson.toJson(list);
        }

        private String getResponseGetPrioritizedTasks(Manager manager) {
            List<Task> list = manager.getPrioritizedTasks();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
            Gson gson = gsonBuilder.create();
            return gson.toJson(list);
        }

        private String getResponseDeleteTaskById(Manager manager, int id, Type type) {
            try {
                Task task = manager.getTaskById(id);
                if(task.getClass() != type) return null;
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
                gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
                Gson gson = gsonBuilder.create();
                manager.deleteTask(id);
                //вернём удалнную задачу
                return gson.toJson(task);
            } catch (TaskFindException exception) {
                return null;
            }
        }

        private String getResponsePostTask(Manager manager, String body, Type type) {

            JsonElement jsonElement = JsonParser.parseString(body);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
            gsonBuilder.registerTypeAdapter(EpicTask.class, new EpicAdapter());
            gsonBuilder.registerTypeAdapter(SubTask.class, new SubtaskAdapter());
            Gson gson = gsonBuilder.create();
            if(jsonObject.keySet().contains("id")) {
                int id = jsonObject.get("id").getAsInt();
                if (type != manager.getTaskById(id).getClass()) return null;
                Task task = gson.fromJson(body, Task.class);
                task.setId(id);
                manager.updateTask(task);
                //вернём изменённую задачу
                return gson.toJson(task);
            } else {
                if (Task.class.equals(type)) {
                    Task task = gson.fromJson(body, Task.class);
                    manager.addTask(task);
                    //вернём добавленную задачу
                    return gson.toJson(task);
                } else if(SubTask.class.equals(type)) {
                    SubTask task = gson.fromJson(body, SubTask.class);
                    task.setEpicTask(manager.getEpicById(task.getEpicId()));
                    manager.addTask(task);
                    //вернём добавленную задачу
                    return gson.toJson(task);
                } else if(EpicTask.class.equals(type)) {
                    EpicTask task = gson.fromJson(body, EpicTask.class);
                    manager.addTask(task);
                    //вернём добавленную задачу
                    return gson.toJson(task);
                }
            }
            return null;
        }



        private Type getType(String type) {
            if(type.equals(types.get(0))) return EpicTask.class;
            if(type.equals(types.get(1)))  return Task.class;
            if(type.equals(types.get(2)))  return SubTask.class;
            return null;
        }

    }
}
