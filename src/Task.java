import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int Id;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        Id = 0;
        status = Status.NEW;

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
        return Id;
    }

    public void setId(int taskId) {
        this.Id = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы к одному ли они относятся
        Task otherTask = (Task) obj; // открываем доступ к полям другого объекта
        return Objects.equals(name, otherTask.name) && // проверяем все поля
                Objects.equals(description, otherTask.description) && // нужно логическое «и»
                (Id == otherTask.Id) && // примитивы сравниваем через ==
                Objects.equals(status, otherTask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, Id, status);
    }

    @Override
    public String toString() {
        return "Задача: " + name
                + ", Описание: " + description
                + ", ID: " + Id
                + ", Статус: " + status;
    }

}
