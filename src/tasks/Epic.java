package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;

    public Epic(String titleTask, String descriptionTask) {
        super(titleTask, descriptionTask);
        subTasksIds = new ArrayList<>();
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
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
