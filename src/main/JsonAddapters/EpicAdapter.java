package JsonAddapters;

import allTasks.EpicTask;
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

public class EpicAdapter extends TypeAdapter<EpicTask> {
    @Override
    public void write(JsonWriter jsonWriter, EpicTask task) throws IOException {
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
        jsonWriter.name("subtasks");
        jsonWriter.beginArray();
        for (SubTask sub : task.getSubTasks()) {
            jsonWriter.beginObject();
            jsonWriter.name("id");
            jsonWriter.value(sub.getId());
            jsonWriter.name("name");
            jsonWriter.value(sub.getName());
            jsonWriter.name("description");
            jsonWriter.value(sub.getDescription());
            jsonWriter.name("status");
            jsonWriter.value(sub.getStatus().toString());
            jsonWriter.name("startTime");
            jsonWriter.value(sub.getStartTime().format(Task.getDATE_TIME_FORMATTER()));
            jsonWriter.name("duration");
            jsonWriter.value(sub.getDuration().toMinutes());
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    @Override
    public EpicTask read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        String name = "";
        String description = "";
        while (jsonReader.hasNext()) {
            String field = jsonReader.nextName();
            if (field.equals("name")) {
                name = jsonReader.nextString();
            } else if (field.equals("description")) {
                description = jsonReader.nextString();
            }  else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new EpicTask(name, description);
    }
}
