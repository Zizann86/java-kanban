import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.DurationTypeAdapter;
import server.HttpTaskServer;
import server.InstantTypeAdapter;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer server = new HttpTaskServer(manager);
    private final Gson jsonMapper = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
    private final HttpClient client = HttpClient.newHttpClient();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Duration.ofMinutes(3), Instant.now());
        // конвертируем её в JSON
        String taskJson = jsonMapper.toJson(task);

        // создаём HTTP-клиент и запрос

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа


        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTaskById() throws IOException, InterruptedException {
        Task task = new Task("Таск1", "Описание", Duration.ofMinutes(3), Instant.now().plusSeconds(90000));

        String json = jsonMapper.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        int id = tasks.getFirst().getId();
        task = new Task(id, "Обновленная", Status.NEW, "Описание", Duration.ofMinutes(3), Instant.now().plusSeconds(5000));

        String json1 = jsonMapper.toJson(task);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json1")
                .POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Обновленная", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Таск", "Описание", Duration.ofMinutes(3), Instant.now().plusSeconds(5400000));

        String json = jsonMapper.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = manager.getAllTasks();
        int id = tasks.getFirst().getId();
        String stringId = String.format("http://localhost:8080/tasks/%d", id);
        request = HttpRequest.newBuilder()
                .uri(URI.create(stringId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testShouldBe406CodePostTask() throws IOException, InterruptedException {
        Task task = new Task("Таск1", "Описание", Duration.ofMinutes(5), Instant.now());

        String json = jsonMapper.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task1 = new Task("Таск2", "Описание", Duration.ofMinutes(5), Instant.now());

        String json1 = jsonMapper.toJson(task1);

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json1")
                .POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 406;
        int actuallyStatusCode = response.statusCode();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код 406");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {

        Epic epic = new Epic("Эпик", "описание эпика", Duration.ofMinutes(20), Instant.now());

        manager.createEpic(epic);


        Task task1 = new Task("Вторая таска", "описание", Duration.ofMinutes(3), Instant.now());

        manager.createTask(task1);

        Task task = new Task("Первая таска", "описание", Duration.ofMinutes(3), Instant.now().plusSeconds(5000));

        manager.createTask(task);

        Subtask subTask = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId(), Duration.ofMinutes(3), Instant.now().plusSeconds(60000));

        manager.createSubtask(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        int actuallyStatusCode = response.statusCode();
        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
    }
}


