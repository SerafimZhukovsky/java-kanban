import task.*;
import enums.*;
import java.io.File;

public class FileTest {
    public static void main(String[] args) {
        try {
            // Создаем временный файл
            File file = File.createTempFile("tasks", ".csv");

            // Тестируем FileBackedTaskManager
            testFileBackedManager(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testFileBackedManager(File file) {
        System.out.println("=== Тестирование FileBackedTaskManager ===");

        // Создаем менеджер и добавляем задачи
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        addTestTasks(manager);

        // Сохраняем в файл
        manager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем загруженные данные
        System.out.println("\nЗагруженные задачи:");
        for (Task task : loadedManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("\nЗагруженные эпики:");
        for (Epic epic : loadedManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nЗагруженные подзадачи:");
        for (Subtask subtask : loadedManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void addTestTasks(FileBackedTaskManager manager) {
        // Добавляем тестовые задачи
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        manager.addTask(task1);

        Epic epic1 = new Epic("Epic 1", "Epic description");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Sub description", Status.NEW);
        subtask1.setEpicID(epic1.getId());
        manager.addSubtask(subtask1);
    }
}
