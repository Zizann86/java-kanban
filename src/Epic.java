import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksIds;

    Epic(String titleTask, String descriptionTask) {
        super(titleTask, descriptionTask);
        subTasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;

    }

    public void addSubtaskId (Subtask subtask) {
        subTasksIds.add(subtask.getId());

    }
    public void clearSubtaskId () {
        subTasksIds.clear();
    }
}
