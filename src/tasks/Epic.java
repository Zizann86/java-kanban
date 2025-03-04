package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;
    private Instant endTime;

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Epic(String titleTask, String descriptionTask) {
        super(titleTask, descriptionTask);
        subTasksIds = new ArrayList<>();
    }

    public Epic(String titleTask, String descriptionTask, Duration duration, Instant startTime) {
        super(titleTask, descriptionTask, duration, startTime);
        subTasksIds = new ArrayList<>();
        this.endTime = getEndTime();
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    public Epic(int id, String name, Status status, String description, Duration duration, Instant startTime) {
        super(id, name, status, description, duration, startTime);
        this.endTime = getEndTime();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    public void addSubtaskId(Subtask subtask) {
        subTasksIds.add(subtask.getId());
    }

    public void clearSubtaskId() {
        subTasksIds.clear();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
