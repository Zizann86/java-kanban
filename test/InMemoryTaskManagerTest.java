import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class InMemoryTaskManagerTest extends TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();
    }

    @Override
    TaskManager getTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void createTask() {
        Task task = new Task("Бег", "Бегать по лесу", Duration.ofMinutes(3), Instant.now());

        Task createTask = taskManager.createTask(task);

        Task actualTask = taskManager.getTaskId(createTask.getId());
        Assertions.assertTrue(actualTask.getId() != 0);
        Assertions.assertEquals(actualTask.getStatus(), Status.NEW);
        Assertions.assertEquals(actualTask.getDescription(), "Бегать по лесу");
        Assertions.assertEquals(actualTask.getName(), "Бег");
    }

    @Test
    void taskInHistoryListShouldNotBeUpdatedAfterTaskUpdate() {
        Task task = new Task("Бег", "Бегать по лесу", Duration.ofMinutes(3), Instant.now());

        taskManager.createTask(task);
        taskManager.getTaskId(task.getId());
        List<Task> historyBef = taskManager.getHistory();
        Task taskInHistory = historyBef.getFirst();
        Status statusInHistoryBeforeUpdate = taskInHistory.getStatus();

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        List<Task> historyAfter = taskManager.getHistory();
        Task taskInHistoryAfterUpdate = historyAfter.getFirst();
        Assertions.assertEquals(statusInHistoryBeforeUpdate, taskInHistoryAfterUpdate.getStatus());

    }

    @Test
    void deleteTaskId() {
        Task task = new Task("Бег", "Бегать по лесу", Duration.ofMinutes(3), Instant.now());

        taskManager.createTask(task);
        taskManager.getAllTasks();
        ArrayList<Task> tasks = taskManager.getAllTasks();

        Task taskCreate = tasks.getFirst();
        Assertions.assertEquals(task, taskCreate);

        taskManager.deleteTaskId(task.getId());
        taskManager.getAllTasks();
        ArrayList<Task> tasksAfterDelete = taskManager.getAllTasks();
        Assertions.assertTrue(tasksAfterDelete.isEmpty());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Порядок дома", "Уборка дома");
        Subtask subtask = new Subtask("Уборка", "Помыть посуду", epic.getId());

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        Status statusBeforeUpdate = subtask.getStatus();

        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        Status statusAfterUpdate = subtask.getStatus();

        Assertions.assertNotEquals(statusBeforeUpdate, statusAfterUpdate);
    }

    @Test
    void EqualToEachOtherIfTheirId() {
        Task task = new Task("Бег", "Бегать по лесу");
        Task otherTask = new Task("Бег", "Гулять по лесу");

        Task taskRun = taskManager.createTask(task);

        Task taskWalk = taskManager.createTask(otherTask);

        Assertions.assertNotEquals(taskRun, taskWalk);
    }
}