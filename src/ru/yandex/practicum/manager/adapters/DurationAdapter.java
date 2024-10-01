package ru.yandex.practicum.manager.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(String.format("%d", duration.toMinutes()));
        } else {
            jsonWriter.value("null");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        try {
            return Duration.parse(jsonReader.nextString());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Неверный формат продолжительности. " +
                    "Пример верного формата: PT30M, где 30 = колличество минут.");
        }
    }
}
