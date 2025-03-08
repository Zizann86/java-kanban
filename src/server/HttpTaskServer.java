package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {
    HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
        httpServer.bind(address, 0);

        Gson jsonMapper = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
        httpServer.createContext("/tasks", new TaskHandler(manager, jsonMapper)); // связываем путь и обработчик
        httpServer.createContext("/epics", new EpicHandler(manager, jsonMapper));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, jsonMapper));
        httpServer.createContext("/history", new HistoryHandler(manager, jsonMapper));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, jsonMapper));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(5);
    }
}
