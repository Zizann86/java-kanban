package tasks;

import java.time.Duration;
import java.time.Instant;


public class Subtask extends Task {
    private int epicId;

    public Subtask(String titleTask, String descriptionTask, int epicId) {
        super(titleTask, descriptionTask);
        this.epicId = epicId;
    }

    public Subtask(String titleTask, String descriptionTask, int epicId, Duration duration, Instant startTime) {
        super(titleTask, descriptionTask, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String titleTask, Status status, String descriptionTask, int epicId) {
        super(id, titleTask, status, descriptionTask);
        this.epicId = epicId;
    }

    public Subtask(int id, String titleTask, Status status, String descriptionTask, int epicId, Duration duration, Instant startTime) {
        super(id, titleTask, status, descriptionTask, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }
}
