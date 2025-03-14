package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskId(int id);

    Epic getEpicId(int id);

    Subtask getSubtaskId(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task deleteTaskId(int id);

    Epic deleteEpicId(int id);

    Subtask deleteSubtaskId(int id);

    ArrayList<Integer> getEpicSubtask(Epic epic);

    List<Subtask> getSubtaskEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
