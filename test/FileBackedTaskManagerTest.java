import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    void shouldSaveAndLoadTasksWithTime() throws IOException {
        // Создаем временный файл
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit(); // Удалится после завершения JVM

        // Создаем менеджер и добавляем задачу
        TaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Task", "Description");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofHours(1));
        manager.addTask(task);

        // Загружаем из файла
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем
        List<Task> prioritized = loadedManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size(), "Должна быть одна задача");

        Task loadedTask = prioritized.get(0);
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }

    @Test
    void shouldThrowExceptionWhenFileInvalid() {
        assertThrows(ManagerSaveException.class, () ->
                FileBackedTaskManager.loadFromFile(new File("invalid_path.txt")));
    }
}
