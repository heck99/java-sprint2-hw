package Functional.Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String API_KEY;
    private String url;


    public KVTaskClient(String url) {
        this.url = url;
        HttpClient client = HttpClient.newHttpClient();
        URI urlRegister = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(urlRegister).GET().build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            // отправляем запрос
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            API_KEY = response.body();
            System.out.println(API_KEY);
        } catch (IOException | InterruptedException exception) {
            System.out.println("о время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException exception) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
    }

    void put(String key, String json) {
        HttpClient client = HttpClient.newHttpClient();

        // получаем ответ в формате XML с помощью заголовка
        URI urlSave = URI.create(url + "/save" + key + "?API_KEY=" + API_KEY);
        // сообщаем серверу, что готовы принять ответ в формате XML
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlSave)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP код ответа: " + response.statusCode());
            System.out.println("Ответ : " + response.body());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        // получаем ответ в формате XML с помощью заголовка
        URI urlSave = URI.create(url + "/load" + key + "?API_KEY=" + API_KEY);
        // сообщаем серверу, что готовы принять ответ в формате XML
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlSave)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return null;
    }
}
