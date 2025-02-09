package tasks;

import java.util.Objects;


public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        id = 0;
        status = Status.NEW;
    }

    public Task(int id, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int taskId) {
        this.id = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return Type.TASK;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы к одному ли они относятся
        Task otherTask = (Task) obj; // открываем доступ к полям другого объекта
        return Objects.equals(name, otherTask.name) && // проверяем все поля
                Objects.equals(description, otherTask.description) && // нужно логическое «и»
                (id == otherTask.id) && // примитивы сравниваем через ==
                Objects.equals(status, otherTask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Задача: " + name
                + ", Описание: " + description
                + ", ID: " + id
                + ", Статус: " + status;
    }
}
