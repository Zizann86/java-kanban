package manager;

import exceptions.TaskInteractionsException;
import exceptions.TaskNotFoundException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int genId = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    //  Получение списка всех задач.

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTask = new ArrayList<>(tasks.values());
        return allTask;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>(epics.values());
        return allEpics;
    }


    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>(subtasks.values());
        return allSubtasks;
    }

    //  Удаление всех задач.

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getEpicId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getEpicId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            epic.setStatus(Status.NEW);
        }
    }

    //  Получение по идентификатору.

    @Override
    public Task getTaskId(int id) {
        Task task = tasks.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (task == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        Task taskForHistory = new Task(task.getName(), task.getDescription());
        historyManager.addToHistory(taskForHistory);
        return task;
    }

    @Override
    public Epic getEpicId(int id) {
        Epic epic = epics.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (epic == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        Epic epicForHistory = new Epic(epic.getName(), epic.getDescription());
        historyManager.addToHistory(epicForHistory);
        return epic;
    }

    @Override
    public Subtask getSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (subtask == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        Subtask subtaskForHistory = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        historyManager.addToHistory(subtaskForHistory);
        return subtask;
    }

    // Создание. Сам объект должен передаваться в качестве параметра.

    @Override
    public Task createTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        if (task.getStartTime() != null && !hasInteractions(task)) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            String errorMessage = "Задача пересекается с другими";
            throw new TaskInteractionsException(errorMessage);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        String errorM = "Задача с id %d не найдена";
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new TaskNotFoundException(errorM);
        }
        int newSubtask = generateId();
        subtask.setId(newSubtask);
        if (subtask.getStartTime() != null && !hasInteractions(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask);
            updateEpicStatus(epic);
            // if (subtask.getStartTime() != null && !hasInteractions(subtask)) {
            prioritizedTasks.add(subtask);
            updateEpicTime(epic);
        } else {
            String errorMessage = "Задача пересекается с другими";
            throw new TaskInteractionsException(errorMessage);
        }
        return subtask;
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    @Override
    public Task updateTask(Task task) {
        if (task.getStartTime() != null && !hasInteractions(task)) {
            int idTask = task.getId();
            if (tasks.containsKey(idTask)) {
                Task existingTask = tasks.get(idTask);
                existingTask.setName(task.getName());
                existingTask.setDescription(task.getDescription());
                existingTask.setStatus(task.getStatus());
                prioritizedTasks.add(task);
            } else {
                String errorMessage = ("Задача с id не найдена");
                    throw new TaskNotFoundException(errorMessage);
            }
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int idEpic = epic.getId();
        if (epics.containsKey(idEpic)) {
            Epic existingEpic = epics.get(idEpic);
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            updateEpicStatus(epic);
        } else {
            String errorMessage = ("Задача с id не найдена");
            throw new TaskNotFoundException(errorMessage);
        }
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        int idSub = subtask.getId();
        int idEpic = subtask.getEpicId();
        if (subtask.getStartTime() != null && !hasInteractions(subtask)) {
            if (subtasks.containsKey(idSub)) {
                Subtask existingSubtask = subtasks.get(idSub);
                existingSubtask.setName(subtask.getName());
                existingSubtask.setDescription(subtask.getDescription());
                Epic epic = epics.get(idEpic);
                ArrayList<Integer> subtaskList = epic.getSubTasksIds();
                epic.setSubTasksIds(subtaskList);
                updateEpicStatus(epic);
                updateEpicTime(epic);
                prioritizedTasks.add(subtask);
            } else {
                String errorMessage = ("Задача с id не найдена");
                throw new TaskNotFoundException(errorMessage);
            }
        }
        return subtask;
    }

    //  Удаление по идентификатору.

    @Override
    public Task deleteTaskId(int id) {
        Task task = tasks.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (task == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
        return task;
    }

    @Override
    public Epic deleteEpicId(int id) {
        Epic epic = epics.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (epic == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        ArrayList<Integer> epicSub = epic.getSubTasksIds();
        epics.remove(id);
        for (Integer idSub : epicSub) {
            subtasks.remove(idSub);
        }
        historyManager.remove(id);
        return epic;
    }

    @Override
    public Subtask deleteSubtaskId(int id) {
        Subtask subtask = subtasks.get(id);
        String errorMessage = String.format("Задача с id %d не найдена", id);
        if (subtask == null) {
            throw new TaskNotFoundException(errorMessage);
        }
        subtasks.remove(id);
        int idEpic = subtask.getEpicId();
        Epic epic = epics.get(idEpic);
        ArrayList<Integer> listId = epic.getSubTasksIds();
        listId.remove((Integer) subtask.getId());
        epic.setSubTasksIds(listId);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);
        return subtask;
    }

    // ------ Получение списка всех подзадач определённого эпика.

    @Override
    public ArrayList<Integer> getEpicSubtask(Epic epic) {
        return epic.getSubTasksIds();
    }

    @Override
    public List<Subtask> getSubtaskEpic(int id) {
        List<Subtask> result = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (int idSubtask : epics.get(id).getSubTasksIds()) {
                result.add(subtasks.get(idSubtask));
            }
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private Integer generateId() {
        return ++genId;
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

    private boolean hasInteractions(Task nTask) {
        return prioritizedTasks.stream().anyMatch(oTask -> {
            return ((oTask.getStartTime().isBefore(nTask.getStartTime()) && (oTask.getEndTime().isAfter(nTask.getStartTime())))) ||
                    (oTask.getStartTime().isBefore(nTask.getEndTime()) && (oTask.getEndTime().isAfter(nTask.getEndTime()))) ||
                    (oTask.getStartTime().isBefore(nTask.getStartTime()) && (oTask.getEndTime().isAfter(nTask.getEndTime()))) ||
                    (oTask.getStartTime().isAfter(nTask.getStartTime()) && (oTask.getEndTime().isBefore(nTask.getEndTime()))) ||
                    (oTask.getStartTime().equals(nTask.getStartTime()));
        });
    }

    private void updateEpicStartTime(Epic epic) {
        Instant startTime = epic.getSubTasksIds().stream()
                .map(subtasks::get)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull) // Фильтруем null значения
                .min(Instant::compareTo)
                .orElse(null);

        epic.setStartTime(startTime);
    }

    private void updateEpicDuration(Epic epic) {
        Duration duration = Duration.ofMinutes(0);
        for (int idSubtask : epic.getSubTasksIds()) {
            Duration durSubTask = subtasks.get(idSubtask).getDuration();
            if (durSubTask != null) {
                duration = duration.plus(durSubTask);
            }
            epic.setDuration(duration);
        }
    }

    private void updateEpicTime(Epic epic) {
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        if (epic.getStartTime() != null && epic.getDuration() != null) {
            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        } else {
            epic.setEndTime(null);
        }
    }
}
