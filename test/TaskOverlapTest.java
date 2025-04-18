import org.junit.jupiter.api.Test;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskOverlapTest {

    TaskManager taskManager = Managers.getDefault();
    @Test
    public void testNonOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.of(2023, 1, 1, 12, 0)); // Начинается позже

        assertFalse(taskManager.isOverlapping(task1, task2), "Задачи не должны пересекаться");
    }

    @Test
    public void testOverlappingTasks() {
        // Задача 1: 10:00 - 12:00
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        task1.setDuration(Duration.ofHours(2));

        // Задача 2: 11:00 - 12:00 (должна пересекаться)
        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.of(2023, 1, 1, 11, 0));
        task2.setDuration(Duration.ofHours(1));

        assertTrue(taskManager.isOverlapping(task1, task2),
                "Задачи должны пересекаться (11:00 внутри 10:00-12:00)");

        // Дополнительные проверки
        // Задача 3: 12:00 - 13:00 (не должна пересекаться)
        Task task3 = new Task("Task 3", "Description");
        task3.setStartTime(LocalDateTime.of(2023, 1, 1, 12, 0));
        task3.setDuration(Duration.ofHours(1));

        assertFalse(taskManager.isOverlapping(task1, task3),
                "Задачи не должны пересекаться (12:00 - граничное время)");

        // Задача 4: 09:00 - 10:00 (не должна пересекаться)
        Task task4 = new Task("Task 4", "Description");
        task4.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        task4.setDuration(Duration.ofHours(1));

        assertFalse(taskManager.isOverlapping(task1, task4),
                "Задачи не должны пересекаться (09:00-10:00)");
    }
}