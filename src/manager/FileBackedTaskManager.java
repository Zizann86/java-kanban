package manager;

import tasks.*;

import exceptions.ManagerReadException;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
            List<String> allLines = Files.readAllLines(file.toPath());
            for (String line : allLines) {
                Task task = fromString(line);
                Type type = task.getType();
                genId = maxId(allLines) + 1;
                if (type.equals(Type.TASK)) {
                    fileBackedTaskManager.tasks.put(task.getId(), task);
                } else if (type.equals(Type.EPIC) && task instanceof Epic epic) {
                    fileBackedTaskManager.epics.put(epic.getId(), epic);
                } else if (type.equals(Type.SUBTASK) && task instanceof Subtask subtask) {
                    fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            String errorMessage = "Ошибка при чтении файла: " + e.getMessage();
            System.out.println(errorMessage);
            throw new ManagerReadException(errorMessage);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task createTask = super.createTask(task);
        save();
        return createTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createEpic = super.createEpic(epic);
        save();
        return createEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createSubtask = super.createSubtask(subtask);
        save();
        return createSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updateSubtask = super.updateSubtask(subtask);
        save();
        return updateSubtask;
    }

    @Override
    public Task deleteTaskId(int id) {
        Task deleteTask = super.deleteTaskId(id);
        save();
        return deleteTask;
    }

    @Override
    public Epic deleteEpicId(int id) {
        Epic deleteEpic = super.deleteEpicId(id);
        save();
        return deleteEpic;
    }

    @Override
    public Subtask deleteSubtaskId(int id) {
        Subtask deleteSubtask = super.deleteSubtaskId(id);
        save();
        return deleteSubtask;
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        Duration duration = Duration.parse(split[5].trim());
        Instant startTime = Instant.parse(split[6].trim());
        switch (type) {
            case "TASK":
                return new Task(id, name, status, description, duration, startTime);
            case "EPIC":
                return new Epic(id, name, status, description, duration, startTime);
            case "SUBTASK":
                return new Subtask(id, name, status, description, Integer.parseInt(split[split.length - 1]), duration, startTime);
            default:
                System.out.println("Ошибка, такого типа не существует");
                return null;
        }
    }

    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getType().equals(Type.SUBTASK) && task instanceof Subtask subtask) {
            sb.append(",").append(subtask.getEpicId());
        }
        sb.append(task.getDuration()).append(",");
        sb.append(task.getStartTime());
        return sb.toString();
    }

    private void save() {
        List<Task> allTasks = getAllTasks();
        String taskAsString = "vseravno perezapishem";
        for (Task task : allTasks) {
            taskAsString = taskToString(task);
        }
        List<Epic> allEpics = getAllEpics();
        for (Epic epic : allEpics) {
            taskAsString = taskToString(epic);
        }
        List<Subtask> allSubtask = getAllSubtasks();
        for (Subtask subtask : allSubtask) {
            taskAsString = taskToString(subtask);
        }
        writeStringToFile(taskAsString);
    }

    private void writeStringToFile(String taskAsString) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(taskAsString);
            fileWriter.write('\n');
        } catch (IOException e) {
            String errorMessage = "Ошибка при сохранение файла: " + e.getMessage();
            System.out.println(errorMessage);
            throw new ManagerSaveException(errorMessage);
        }
    }

    private static int maxId(List<String> allLines) {
        int maxCounter = 0;
        for (String allLine : allLines) {
            String[] split = allLine.split(",");
            int id = Integer.parseInt(split[0]);
            if (id > maxCounter) {
                maxCounter = id;
            }
        }
        return maxCounter;
    }
}
