public class Main {


    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();
        Task garbage = new Task("Мусор", "Вынести мусор");
        manager.createTask(garbage);
        System.out.println(manager.getAllTasks());
        Task exercise = new Task("Бег", "Бегать по лесу");
        manager.createTask(exercise);
        System.out.println(manager.getAllTasks());

        Epic vacation = new Epic("Отпуск", "Лететь на море");
        manager.createEpic(vacation);
        System.out.println(manager.getAllEpics());
        Subtask buyTicket = new Subtask("Путевка", "Купить билеты в Тайланд", vacation.getId());
        manager.createSubtask(buyTicket);
        System.out.println(manager.getAllSubtasks());
        Subtask collectThings = new Subtask("Собрать вещи", "Положить вещи в сумку", vacation.getId());
        manager.createSubtask(collectThings);

        Epic removal = new Epic("Переезд", "Переезд в загородный дом");
        manager.createEpic(removal);
        Subtask packaging = new Subtask("Упаковка", "Собрать вещи по коробкам", removal.getId());
        manager.createSubtask(packaging);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        exercise.setStatus(Status.IN_PROGRESS);
        manager.updateTask(exercise);
        collectThings.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(collectThings);
        buyTicket.setStatus(Status.IN_PROGRESS);
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
        System.out.println(manager.getAllSubtasks());

    }
}
