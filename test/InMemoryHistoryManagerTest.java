import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import enums.Status;
import task.Task;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task washFloor = new Task("Помыть полы", "С новым средством");
        washFloor.setId(1);
        taskManager.addTask(washFloor);

        taskManager.getTaskByID(washFloor.getId());

        Task updatedTask = new Task("Не забыть помыть полы", "Можно и без средства", Status.IN_PROGRESS);
        updatedTask.setId(washFloor.getId());
        taskManager.updateTask(updatedTask);

        List<Task> tasks = taskManager.getHistory();
        assertNotNull(tasks, "История не должна быть null");
        assertEquals(1, tasks.size(), "В истории должна быть одна задача");

        Task oldTask = tasks.get(0);
        assertEquals(washFloor.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
        assertEquals(washFloor.getDescription(), oldTask.getDescription(), "В истории не сохранилась старая версия задачи");
    }

    @Test
    public void addTaskShouldAddTaskToHistory() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не добавлена в историю");
        assertEquals(task, history.get(0), "Задача в истории не совпадает с добавленной");
    }

    @Test
    public void addTaskShouldRemoveDuplicateTask() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дубликат задачи не удалён из истории");
    }

    @Test
    public void removeTaskShouldRemoveTaskFromHistory() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "Задача не удалена из истории");
    }

    @Test
    public void getHistoryShouldReturnTasksInOrderOfAddition() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertFalse(history.isEmpty(), "История не должна быть пустой");

        assertEquals(task1, history.get(0), "Порядок задач в истории нарушен");
        assertEquals(task2, history.get(1), "Порядок задач в истории нарушен");
    }

    @Test
    public void updateTaskShouldNotAffectHistory() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(1);
        taskManager.addTask(task);

        taskManager.getTaskByID(task.getId());

        task.setName("Updated Task 1");
        task.setDescription("Updated Description 1");

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertFalse(history.isEmpty(), "История не должна быть пустой");

        Task savedTask = history.get(0);
        assertEquals("Task 1", savedTask.getName(), "История содержит обновлённую задачу");
        assertEquals("Description 1", savedTask.getDescription(), "История содержит обновлённую задачу");
    }
}


