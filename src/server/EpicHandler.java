package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public EpicHandler(TaskManager taskManager, Gson jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGetEpics(exchange);
                    break;
                case "POST":
                    handlePostEpics(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpics(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            sendText(exchange, "Ошибка сервера", 500);
        } finally {
            exchange.close();
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");
        if (urlParts.length == 2) {
            List<Epic> allEpics = taskManager.getAllEpics();
            String json = jsonMapper.toJson(allEpics);
            sendText(exchange, json, 200);
        } else if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            Epic epicById = taskManager.getEpicId(id);
            String json = jsonMapper.toJson(epicById);
            sendText(exchange, json, 200);
        } else if (urlParts.length > 3) {
            String subtask = urlParts[3];

            if (subtask.equals("subtask")) {
                int id = Integer.parseInt(urlParts[2]);
                List<Subtask> subtasksByEpic = taskManager.getSubtaskEpic(id);
                String json = jsonMapper.toJson(subtasksByEpic);
                sendText(exchange, json, 200);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostEpics(HttpExchange exchange) throws IOException {

        byte[] inputStream = exchange.getRequestBody().readAllBytes();
        String body = new String(inputStream, StandardCharsets.UTF_8);
        Epic deserialized = jsonMapper.fromJson(body, Epic.class);
        if (deserialized.getId() == 0) {
            taskManager.createEpic(deserialized);
            sendText(exchange, "Задача добавлена", 201);
        } else {
            taskManager.updateEpic(deserialized);
            sendText(exchange, "Задача обновлена", 201);
        }

    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            taskManager.deleteEpicId(id);
            sendText(exchange, "Задача удалена", 200);
        }

        if (urlParts.length == 2) {
            taskManager.deleteAllEpics();
            sendText(exchange, "Все задачи удалены", 200);
        }
    }
}
