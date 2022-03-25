package JsonAddapters;

import allTasks.EpicTask;
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new DateFormatter());
        Gson gson = gsonBuilder.create();
        jsonWriter.value(gson.toJson(task));
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
