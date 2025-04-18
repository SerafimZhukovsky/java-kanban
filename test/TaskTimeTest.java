import enums.Status;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TaskTimeTest {

    @Test
    public void testTaskDurationAndTime() {
        Task task = new Task("Test", "Description");
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));

        assertEquals(Duration.ofMinutes(30), task.getDuration());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 30), task.getEndTime());
    }

    @Test
    public void testEpicTimeCalculation() {
        Epic epic = new Epic("Epic", "Description");
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.NEW);
        subtask1.setDuration(Duration.ofMinutes(30));
        subtask1.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));

        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.NEW);
        subtask2.setDuration(Duration.ofMinutes(60));
        subtask2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 0));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        assertEquals(Duration.ofMinutes(90), epic.getDuration());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), epic.getEndTime());
    }

    @Test
    public void testTaskWithoutTimeNotInPrioritizedList() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Task", "Description"); // Без startTime
        manager.addTask(task);
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Задачи без времени не должны попадать в список");
    }
}