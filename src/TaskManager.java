import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int genId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    private Integer generateId() {
        return ++genId;
    }

    //  Получение списка всех задач.

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTask = new ArrayList<>(tasks.values());
        return allTask;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>(epics.values());
        return allEpics;
    }


    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>(subtasks.values());
        return allSubtasks;
    }

    //  Удаление всех задач.

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            epic.setStatus(Status.NEW);
        }
    }

    //  Получение по идентификатору.

    public Task getTaskId(int id) {
        Task task = tasks.get(id);
        return task;
    }

    public Epic getEpicId(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    public Subtask getSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);
        return subtask;
    }

    // Создание. Сам объект должен передаваться в качестве параметра.

    public Task createTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }
        int newSubtask = generateId();
        subtask.setId(newSubtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    public Task updateTask(Task task) {
        int idTask = task.getId();
        if (tasks.containsKey(idTask)) {
            Task existingTask = tasks.get(idTask);
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    public Epic updateEpic(Epic epic) {
        int idEpic = epic.getId();
        if (epics.containsKey(idEpic)) {
            Epic existingEpic = epics.get(idEpic);
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            updateEpicStatus(epic);
        }
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        int idSub = subtask.getId();
        int idEpic = subtask.getEpicId();
        if (subtasks.containsKey(idSub)) {
            Subtask existingSubtask = subtasks.get(idSub);
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            Epic epic = epics.get(idEpic);
            ArrayList<Integer> subtaskList = epic.getSubTasksIds();
            epic.setSubTasksIds(subtaskList);
            updateEpicStatus(epic);
        }
        return subtask;
    }

    //  Удаление по идентификатору.

    public Task deleteTaskId(int id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }

    public Epic deleteEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> epicSub = epic.getSubTasksIds();
        epics.remove(id);
        for (Integer idSub : epicSub) {
            subtasks.remove(idSub);
        }
        return epic;
    }

    public Subtask deleteSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        int idEpic = subtask.getEpicId();
        Epic epic = epics.get(idEpic);
        ArrayList<Integer> listId = epic.getSubTasksIds();
        listId.remove((Integer)subtask.getId());
        epic.setSubTasksIds(listId);
        updateEpicStatus(epic);
        return subtask;
    }

    // ------ Получение списка всех подзадач определённого эпика.

    public ArrayList<Integer> getEpicSubtask(Epic epic) {
        return epic.getSubTasksIds();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasksIds().isEmpty()) {
            return;
        }
        int doneCount = 0;
        int newCount = 0;
        ArrayList<Integer> listIdSubtask = epic.getSubTasksIds();
        for (Integer idSubtask : listIdSubtask) {
            Subtask subtaskStatusDone = subtasks.get(idSubtask);
            if (subtaskStatusDone.getStatus() == Status.DONE) {
                doneCount++;
            }
            Subtask subtaskStatusNew = subtasks.get(idSubtask);
            if (subtaskStatusNew.getStatus() == Status.NEW) {
                newCount++;
            }
        }
        if (doneCount == listIdSubtask.size()) {
            epic.setStatus(Status.DONE);
        } else if (newCount == listIdSubtask.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}