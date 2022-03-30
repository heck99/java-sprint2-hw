package JsonAddapters;

import allTasks.Status;
import allTasks.SubTask;
import allTasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskAdapter extends TypeAdapter<Task> {
    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value(task.getId());
        jsonWriter.name("name");
        jsonWriter.value(task.getName());
        jsonWriter.name("description");
        jsonWriter.value(task.getDescription());
        jsonWriter.name("status");
        jsonWriter.value(task.getStatus().toString());
        jsonWriter.name("startTime");
        jsonWriter.value(task.getStartTime().format(Task.getDATE_TIME_FORMATTER()));
        jsonWriter.name("duration");
        jsonWriter.value(task.getDuration().toMinutes());
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String name = "";
        String description = "";
        LocalDateTime startTime = null;
        Duration duration = null;
        Status status = Status.NEW;
        while (jsonReader.hasNext()) {
            String field = jsonReader.nextName();
            if (field.equals("name")) {
                name = jsonReader.nextString();
            } else if (field.equals("description")) {
                description = jsonReader.nextString();
            } else if (field.equals("startTime")) {
                startTime = LocalDateTime.parse(jsonReader.nextString(), Task.getDATE_TIME_FORMATTER());
            } else if (field.equals("duration")) {
                duration = Duration.ofMinutes(jsonReader.nextLong());
            } else if (field.equals("status")) {
                status = Status.valueOf(jsonReader.nextString());
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();

        return new Task(name,description, status, startTime, duration);
    }
}
