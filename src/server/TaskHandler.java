package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskInteractionsException;
import exceptions.TaskNotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private Gson jsonMapper;

    public TaskHandler(TaskManager taskManager, Gson jsonMapper) {
        this.taskManager = taskManager;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
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

    private void handleGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            Task taskById = taskManager.getTaskId(id);
            String json = jsonMapper.toJson(taskById);
            sendText(exchange, json, 200);
        }

        if (urlParts.length == 2) {
            List<Task> allTask = taskManager.getAllTasks();
            String json = jsonMapper.toJson(allTask);
            sendText(exchange, json, 200);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        byte[] inputStream = exchange.getRequestBody().readAllBytes();
        String body = new String(inputStream, StandardCharsets.UTF_8);
        Task deserialized = jsonMapper.fromJson(body, Task.class);
        if (deserialized.getId() == 0) {
            taskManager.createTask(deserialized);
            sendText(exchange, "Задача добавлена", 201);
        } else {
            taskManager.updateTask(deserialized);
            sendText(exchange, "Задача обновлена", 201);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        String[] urlParts = path.split("/");

        if (urlParts.length == 3) {
            int id = Integer.parseInt(urlParts[2]);
            taskManager.deleteTaskId(id);
            sendText(exchange, "Задача удалена", 200);
        }

        if (urlParts.length == 2) {
            taskManager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены", 200);
        }
    }
}