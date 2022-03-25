package JsonAddapters;

import allTasks.Task;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class DateFormatter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime date) throws IOException {
        jsonWriter.value(date.format(Task.getDATE_TIME_FORMATTER()));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.toString(), Task.getDATE_TIME_FORMATTER());
    }
}



