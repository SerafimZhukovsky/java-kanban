import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }
    @Test
    abstract void shouldAddTask();

    @Test
    void shouldPrioritizeTasksByTime() {
        Task earlyTask = new Task("Early", "Desc");
        earlyTask.setStartTime(LocalDateTime.now());
        taskManager.addTask(earlyTask);

        Task lateTask = new Task("Late", "Desc");
        lateTask.setStartTime(LocalDateTime.now().plusHours(1));
        taskManager.addTask(lateTask);

        assertEquals(earlyTask, taskManager.getPrioritizedTasks().get(0));
    }
}

