import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTaskManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
       // InetSocketAddress address = new InetSocketAddress("localhost", 8080);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        TaskManager manager = Managers.getDefault();
        Gson jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
        httpServer.start();

        httpServer.createContext("/tasks", new TaskHandler(manager, jsonMapper)); // связываем путь и обработчик
        httpServer.createContext("/epics", new EpicHandler(manager, jsonMapper));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, jsonMapper));
        httpServer.createContext("/history", new HistoryHandler(manager, jsonMapper));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, jsonMapper));

        Epic epic = new Epic("Эпик", "описание эпика", Duration.ofMinutes(20), Instant.now());
        manager.createTask(new Task("Первая таска", "описание", Duration.ofMinutes(3), Instant.now()));
        manager.createTask(new Task("Вторая таска", "описание", Duration.ofMinutes(3), Instant.now().plusSeconds(80000)));
        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now().plusSeconds(9000000)));

       /* File file = new File("task.csv");

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        FileBackedTaskManager.loadFromFile(file);
        Task task = new Task("Задача", "описание задачи");
        Epic epic = new Epic("Эпик", "описание эпика");
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId());
        Subtask subtask2 = new Subtask("Сабтаск2", "описание сабтаска2", epic.getId());
        fileBackedTaskManager.createSubtask(subtask1);
        fileBackedTaskManager.createSubtask(subtask2);

        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileBackedTaskManager1.getAllTasks());
        System.out.println(fileBackedTaskManager1.getAllEpics());
        System.out.println(fileBackedTaskManager1.getAllSubtasks());*/
    }
}
