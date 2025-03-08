
import static org.junit.jupiter.api.Assertions.*;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

abstract class TaskManagerTest<T extends TaskManager> {
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected TaskManager taskManager;

    abstract TaskManager getTaskManager();

    @BeforeEach
    void add() {
        task = new Task("Таск", "описание таска", Duration.ofMinutes(3), Instant.now());
        epic = new Epic("Эпик", "описание эпика", Duration.ofMinutes(20), Instant.now());

        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        taskManager.createTask(task);

        Task verification = taskManager.getTaskId(task.getId());

        assertNotNull(verification);
        assertEquals(Status.NEW, verification.getStatus());
        assertEquals("Таск", verification.getName());
        assertEquals("описание таска", verification.getDescription());
    }

    @Test
    void createEpic() {
        taskManager.createEpic(epic);

        Epic verification = taskManager.getEpicId(epic.getId());

        assertNotNull(verification);
        assertEquals("Эпик", verification.getName());
        assertEquals("описание эпика", verification.getDescription());
        assertEquals(Status.NEW, verification.getStatus());
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now());
        taskManager.createSubtask(subtask1);

        Subtask verification = taskManager.getSubtaskId(subtask1.getId());

        assertNotNull(verification);
        assertEquals("Сабтаск1", verification.getName());
        assertEquals("описание сабтаска1", verification.getDescription());
        assertEquals(Status.NEW, verification.getStatus());
    }

    @Test
    void updateTask() {
        final String name = "Новый таск";
        final String description = "Описание нового таска";
        final Status status = Status.DONE;
        taskManager.createTask(task);
        task.setName(name);
        task.setDescription(description);
        task.setStatus(status);

        Task verification = taskManager.updateTask(task);
        taskManager.updateTask(task);

        assertNotNull(verification);
        assertEquals(name, verification.getName());
        assertEquals(description, verification.getDescription());
        assertEquals(Status.DONE, verification.getStatus());
    }

    @Test
    void updateEpic() {
        final String name = "Новый эпик";
        final String description = "Описание нового эпика";
        final Status status = Status.DONE;
        taskManager.createEpic(epic);
        epic.setName(name);
        epic.setDescription(description);
        epic.setStatus(status);

        Epic verification = taskManager.updateEpic(epic);
        taskManager.updateEpic(epic);

        assertNotNull(verification);
        assertEquals(name, verification.getName());
        assertEquals(description, verification.getDescription());
        assertEquals(Status.DONE, verification.getStatus());
    }

    @Test
    void updateSubtask() {
        final String name = "Новый сабтаск";
        final String description = "Описание нового сабтаска";
        final Status status = Status.DONE;
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now());
        taskManager.createSubtask(subtask1);
        subtask1.setName(name);
        subtask1.setDescription(description);
        subtask1.setStatus(status);

        Subtask verification = taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask1);

        assertNotNull(verification);
        assertEquals(name, verification.getName());
        assertEquals(description, verification.getDescription());
        assertEquals(Status.DONE, verification.getStatus());
        assertEquals(epic.getId(), verification.getEpicId());
    }

    @Test
    void deleteTaskById() {
        taskManager.createTask(task);

        taskManager.deleteTaskId(task.getId());

        assertFalse(taskManager.getAllTasks().contains(task));
        assertEquals(0, taskManager.getAllTasks().size());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        taskManager.createEpic(epic);

        taskManager.deleteEpicId(epic.getId());

        assertFalse(taskManager.getAllEpics().contains(epic));
        assertEquals(0, taskManager.getAllEpics().size());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void deleteSubtaskById() {
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now());
        taskManager.createSubtask(subtask1);

        taskManager.deleteSubtaskId(subtask1.getId());

        assertFalse(taskManager.getAllSubtasks().contains(subtask));
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task("Таск 1", "Описание таск1", Duration.ofMinutes(3), Instant.now().plusSeconds(90000));
        taskManager.createTask(task);
        taskManager.createTask(task1);

        taskManager.deleteAllTasks();

        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика1");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        taskManager.deleteAllEpics();

        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика1");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now());
        Subtask subtask2 = new Subtask("Сабтаск2", "описание сабтаска2", epic.getId(), Duration.ofMinutes(3), Instant.now().plusSeconds(50000));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteAllSubtasks();

        Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty());
    }
}
