package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String titleTask, String descriptionTask, int epicId) {
        super(titleTask, descriptionTask);
        this.epicId = epicId;
    }

    public Subtask(int id, String titleTask, Status status, String descriptionTask, int epicId) {
        super(id, titleTask, status, descriptionTask);
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
