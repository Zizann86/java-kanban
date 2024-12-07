public class Subtask extends Task {
    private int epicId;

    Subtask(String titleTask, String descriptionTask, int epicId) {
        super(titleTask, descriptionTask);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
