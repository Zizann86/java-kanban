package tasks;


import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private Instant startTime;

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Instant getStartTime() {
        return startTime;
    }


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        id = 0;
        status = Status.NEW;
    }

    public Task(String name, String description, Duration duration, Instant startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        id = 0;
        status = Status.NEW;
    }

    public Task(int id, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(int id, String name, Status status, String description, Duration duration, Instant startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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

    public Instant getEndTime() {
        if (this.startTime != null && this.duration != null) {
            return this.startTime.plus(this.duration);
        } else {
            return null;
        }
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
                + ", Статус: " + status
               // + ", Старт: " + ZonedDateTime.ofInstant(getStartTime(), ZoneId.systemDefault()).format(formatter)
                + ", Старт: " + ZonedDateTime.ofInstant(startTime, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy/ HH:mm"))
                + ", Продолжительность: " + duration.toMinutes() + " минут";
    }
}
