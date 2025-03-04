import manager.FileBackedTaskManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private File file;

    private final FileBackedTaskManager manager = createTaskManager();

    @Test
    public void testLoad() {
        Task task = new Task("Таск", "Описание", Duration.ofMinutes(30), Instant.now());
        manager.createTask(task);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(task, loadedManager.getTaskId(task.getId()));
    }

    @Override
    TaskManager getTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    protected FileBackedTaskManager createTaskManager() {
        try {
            file = Files.createTempFile("task", ".csv").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
        return new FileBackedTaskManager(new InMemoryHistoryManager(), file);
    }
}

