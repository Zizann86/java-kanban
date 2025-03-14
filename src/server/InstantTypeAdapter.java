package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class InstantTypeAdapter extends TypeAdapter<Instant> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, final Instant instant) throws IOException {
        if (instant != null) {
            String formattedDate = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC).format(formatter);
            jsonWriter.value(formattedDate);
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        String dateTimeString = jsonReader.nextString();
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
            return instant;
        } catch (
                DateTimeParseException e) {
            System.out.println("Строка НЕ является корректной датой: " + e.getMessage());
        }
        return null;
    }
}