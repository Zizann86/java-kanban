package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public HistoryHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && path.length == 2) {
            List<Task> history = taskManager.getHistory();
            String json = jsonMapper.toJson(history);
            sendText(exchange, json, 200);
        } else {
            sendText(exchange, "Неправильный формат запроса", 404);
        }
    }
}
