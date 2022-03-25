package JsonAddapters;

import allTasks.EpicTask;
import allTasks.Status;
import allTasks.SubTask;
import allTasks.Task;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<SubTask> {
    @Override
    public void write(JsonWriter jsonWriter, SubTask task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value(task.getId());
        jsonWriter.name("name");
        jsonWriter.value(task.getName());
        jsonWriter.name("status");
        jsonWriter.value(task.getStatus().toString());
        jsonWriter.name("description");
        jsonWriter.value(task.getDescription());
        jsonWriter.name("startTime");
        jsonWriter.value(task.getStartTime().format(Task.getDATE_TIME_FORMATTER()));
        jsonWriter.name("duration");
        jsonWriter.value(task.getDuration().toMinutes());
        jsonWriter.name("epicId");
        jsonWriter.value(task.getEpicTask().getId());
        jsonWriter.endObject();
    }

    @Override
    public SubTask read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String name = "";
        String description = "";
        LocalDateTime startTime = null;
        Duration duration = null;
        Status status = Status.IN_PROGRESS;
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
        EpicTask epic = new EpicTask("","");
        return new SubTask(name,description, status, startTime, duration, epic);
    }
}