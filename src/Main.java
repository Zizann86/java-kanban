import manager.FileBackedTaskManager;
import manager.InMemoryHistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        /*manager.TaskManager manager = manager.Managers.getDefault();
        tasks.Task garbage = new tasks.Task("Мусор", "Вынести мусор");
        manager.createTask(garbage);
        System.out.println(manager.getAllTasks());
        tasks.Task exercise = new tasks.Task("Бег", "Бегать по лесу");
        manager.createTask(exercise);
        System.out.println(manager.getAllTasks());

        tasks.Epic vacation = new tasks.Epic("Отпуск", "Лететь на море");
        manager.createEpic(vacation);
        System.out.println(manager.getAllEpics());
        tasks.Subtask buyTicket = new tasks.Subtask("Путевка", "Купить билеты в Тайланд", vacation.getId());
        manager.createSubtask(buyTicket);
        System.out.println(manager.getAllSubtasks());
        tasks.Subtask collectThings = new tasks.Subtask("Собрать вещи", "Положить вещи в сумку", vacation.getId());
        manager.createSubtask(collectThings);

        tasks.Epic removal = new tasks.Epic("Переезд", "Переезд в загородный дом");
        manager.createEpic(removal);
        tasks.Subtask packaging = new tasks.Subtask("Упаковка", "Собрать вещи по коробкам", removal.getId());
        manager.createSubtask(packaging);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        exercise.setStatus(tasks.Status.IN_PROGRESS);
        manager.updateTask(exercise);
        collectThings.setStatus(tasks.Status.IN_PROGRESS);
        manager.updateSubtask(collectThings);
        buyTicket.setStatus(tasks.Status.IN_PROGRESS);
        manager.updateSubtask(buyTicket);

        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        manager.deleteSubtaskId(collectThings.getId());

        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        manager.deleteEpicId(vacation.getId());

        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());*/

        File file = new File("task.csv");

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
        System.out.println(fileBackedTaskManager1.getAllSubtasks());
    }
}
