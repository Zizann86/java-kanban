import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
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
    }
}
