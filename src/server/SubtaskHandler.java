package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskInteractionsException;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson jsonMapper;

    public SubtaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.jsonMapper = jsonMapper;
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGetSubtask(exchange);
                    break;
                case "POST":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE":
                    handleDeleteSubtask(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (TaskInteractionsException e) {
            sendHasInteractions(exchange);

        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            sendText(exchange, "Ошибка сервера", 500);

        } finally {
            exchange.close();
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            Subtask taskById = taskManager.getSubtaskId(id);
            String json = jsonMapper.toJson(taskById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            List<Subtask> allSubtask = taskManager.getAllSubtasks();
            String json = jsonMapper.toJson(allSubtask);
            sendText(exchange, json, 200);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        byte[] inputStream = exchange.getRequestBody().readAllBytes();
        String body = new String(inputStream, StandardCharsets.UTF_8);
        Subtask deserialized = jsonMapper.fromJson(body, Subtask.class);
        if (deserialized.getId() == 0) {
            taskManager.createSubtask(deserialized);
            sendText(exchange, "Задача добавлена", 201);
        } else {
            taskManager.updateTask(deserialized);
            sendText(exchange, "Задача обновлена", 200);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            taskManager.deleteSubtaskId(id);
            sendText(exchange, "Задача удалена", 200);
        }

        if (urlParts.length == 2) {
            taskManager.deleteAllSubtasks();
            sendText(exchange, "Все задачи удалены", 200);
        }
    }
}
