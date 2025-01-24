import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
     Task task;
     Epic epic;
     Subtask subtask1Epic;
     Subtask subtask2Epic;

     HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Задача", "описание задачи");
        epic = new Epic("Эпик", "описание эпика");
        subtask1Epic = new Subtask("Сабтаск1", "описание сабтаска1", epic.getId());
        subtask2Epic = new Subtask("Сабтаск2", "описание сабтаска2", epic.getId());
    }

    @Test
    void addToHistory () {
        task.setId(0);
        historyManager.addToHistory(task);
        epic.setId(1);
        historyManager.addToHistory(epic);
        subtask1Epic.setId(2);
        historyManager.addToHistory(subtask1Epic);
        subtask2Epic.setId(3);
        historyManager.addToHistory(subtask2Epic);

        final List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "История не пустая.");

        // Проверка заодно на дубль:
        historyManager.addToHistory(task);
        assertEquals(4, history.size(), "Задача попала в историю");
    }

    @Test
    void getHistory() {
        List<Task> forComparisonHistory = new ArrayList<>();
        task.setId(0);
        historyManager.addToHistory(task);
        forComparisonHistory.add(task);
        epic.setId(1);
        historyManager.addToHistory(epic);
        forComparisonHistory.add(epic);
        subtask1Epic.setId(2);
        historyManager.addToHistory(subtask1Epic);
        forComparisonHistory.add(subtask1Epic);
        subtask2Epic.setId(3);
        historyManager.addToHistory(subtask2Epic);
        forComparisonHistory.add(subtask2Epic);

        List<Task> listHistory = historyManager.getHistory();
        assertNotNull(listHistory, "Пусто");
        assertEquals(forComparisonHistory.size(), listHistory.size(), "Не совпадают размеры списков");
        assertTrue(forComparisonHistory.containsAll(listHistory), "Списки не совпадают");
    }
    @Test
    void remove() {
        task.setId(0);
        historyManager.addToHistory(task);
        epic.setId(1);
        historyManager.addToHistory(epic);
        subtask1Epic.setId(2);
        historyManager.addToHistory(subtask1Epic);
        subtask2Epic.setId(3);
        historyManager.addToHistory(subtask2Epic);

        historyManager.remove(subtask2Epic.getId());
        assertFalse(historyManager.getHistory().contains(subtask2Epic), "Задача не удалена");
        assertEquals(3, historyManager.getHistory().size(), "Размеры списков не совпадают");
    }
}
