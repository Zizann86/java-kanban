package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public PrioritizedHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && path.length == 2) {
            List<Task> tasks = taskManager.getPrioritizedTasks();
            String json = jsonMapper.toJson(tasks);
            sendText(exchange, json, 200);
        } else {
            sendText(exchange, "Неправильный формат запроса", 404);
        }
    }
}
