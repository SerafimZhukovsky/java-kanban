import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import enums.Status;
import task.Task;
import task.Subtask;
import task.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        final Task task = taskManager.addTask(new Task("Test addNewTask", "Test addNewTask description"));
        assertNotNull(task.getId(), "ID задачи не был установлен");
        final Task savedTask = taskManager.getTaskByID(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getName(), savedTask.getName(), "Имена задач не совпадают.");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания задач не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task.getName(), tasks.get(0).getName(), "Имена задач не совпадают.");
        assertEquals(task.getDescription(), tasks.get(0).getDescription(), "Описания задач не совпадают.");
    }

    @Test
    void addNewEpicAndSubtasks() {
        final Epic flatRenovation = taskManager.addEpic(new Epic("Сделать ремонт",
                "Нужно успеть за отпуск"));
        final Subtask flatRenovationSubtask1 = taskManager.addSubtask(new Subtask("Поклеить обои",
                "Обязательно светлые!", flatRenovation.getId()));
        final Subtask flatRenovationSubtask2 = taskManager.addSubtask(new Subtask("Установить новую технику",
                "Старую продать на Авито", flatRenovation.getId()));
        final Subtask flatRenovationSubtask3 = taskManager.addSubtask(new Subtask("Заказать книжный шкаф", "Из темного дерева",
                flatRenovation.getId()));
        final Epic savedEpic = taskManager.getEpicByID(flatRenovation.getId());
        final Subtask savedSubtask1 = taskManager.getSubtaskByID(flatRenovationSubtask1.getId());
        final Subtask savedSubtask2 = taskManager.getSubtaskByID(flatRenovationSubtask2.getId());
        final Subtask savedSubtask3 = taskManager.getSubtaskByID(flatRenovationSubtask3.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertEquals(flatRenovation, savedEpic, "Эпики не совпадают.");
        assertEquals(flatRenovationSubtask1, savedSubtask1, "Подзадачи не совпадают.");
        assertEquals(flatRenovationSubtask3, savedSubtask3, "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(flatRenovation, epics.get(0), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask1, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void deleteTasksShouldReturnEmptyList() {
        taskManager.addTask(new Task("Купить книги", "Список в заметках"));
        taskManager.addTask(new Task("Помыть полы", "С новым средством"));
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();
        assertTrue(tasks.isEmpty(), "После удаления задач список должен быть пуст.");
    }

    @Test
    public void deleteEpicsShouldReturnEmptyList() {
        taskManager.addEpic(new Epic("Сделать ремонт", "Нужно успеть за отпуск"));
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpics();
        assertTrue(epics.isEmpty(), "После удаления эпиков список должен быть пуст.");
    }

    @Test
    public void deleteSubtasksShouldReturnEmptyList() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic); // Добавляем эпик в менеджер
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.addSubtask(subtask); // Добавляем подзадачу в менеджер
        taskManager.deleteSubtaskByID(subtask.getId()); // Удаляем подзадачу
        Epic savedEpic = taskManager.getEpicByID(epic.getId()); // Получаем эпик из менеджера
        assertFalse(savedEpic.getSubtaskList().contains(subtask), "ID подзадачи не удалён из эпика");
    }

    @Test
    void TaskCreatedAndTaskAddedShouldHaveSameVariables() {
        Task expected = new Task("Помыть полы", "С новым средством", Status.DONE);
        Task addedTask = taskManager.addTask(expected); // Добавляем задачу в менеджер
        assertNotNull(addedTask.getId(), "ID задачи не был установлен"); // Проверяем, что ID был установлен

        List<Task> list = taskManager.getTasks();
        Task actual = list.get(0);

        assertEquals(addedTask.getId(), actual.getId(), "ID задач не совпадают");
        assertEquals(expected.getName(), actual.getName(), "Имена задач не совпадают");
        assertEquals(expected.getDescription(), actual.getDescription(), "Описания задач не совпадают");
        assertEquals(expected.getStatus(), actual.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    public void deleteSubtaskShouldRemoveItsIdFromEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtaskByID(subtask.getId());
        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertFalse(savedEpic.getSubtaskList().contains(subtask), "ID подзадачи не удалён из эпика");
    }

    @Test
    public void updateTaskBySettersShouldNotAffectManager() {
        Task task = new Task("Task 1", "Description 1");

        Task addedTask = taskManager.addTask(task);
        assertNotNull(addedTask, "Задача не была добавлена в менеджер");
        assertNotNull(addedTask.getId(), "ID задачи не был установлен");

        task.setName("Updated Task 1");
        task.setDescription("Updated Description 1");

        Task savedTask = taskManager.getTaskByID(addedTask.getId());
        assertNotNull(savedTask, "Задача не найдена в менеджере");

        assertEquals("Task 1", savedTask.getName(), "Изменение задачи через сеттеры повлияло на менеджер");
        assertEquals("Description 1", savedTask.getDescription(), "Изменение задачи через сеттеры повлияло на менеджер");
    }

    @Test
    public void updateSubtaskStatusShouldUpdateEpicStatus() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.addSubtask(subtask);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика не обновлён");
    }

    @Test
    public void deleteAllTasksShouldClearTaskList() {
        taskManager.addTask(new Task("Task 1", "Description 1"));
        taskManager.addTask(new Task("Task 2", "Description 2"));
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();
        assertTrue(tasks.isEmpty(), "Список задач не пуст после удаления");
    }

    @Test
    public void testGetPrioritizedTasks() {
        // Создаем задачи
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        task1.setDuration(Duration.ofMinutes(30));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task2.setDuration(Duration.ofHours(1));

        Task taskWithoutTime = new Task("Task 3", "Description");

        // Добавляем задачи в менеджер
        Task addedTask1 = taskManager.addTask(task1);
        Task addedTask2 = taskManager.addTask(task2);
        taskManager.addTask(taskWithoutTime);

        // Получаем приоритизированный список
        List<Task> prioritized = taskManager.getPrioritizedTasks();

        // Проверяем размер списка
        assertEquals(2, prioritized.size(), "Должны быть только задачи с указанным временем");

        // Проверяем порядок задач (используем добавленные задачи с ID)
        assertEquals(addedTask2, prioritized.get(0), "Первой должна быть задача с ранним startTime");
        assertEquals(addedTask1, prioritized.get(1), "Второй — с поздним startTime");
    }

    @Test
    public void testUpdateTaskInPrioritizedList() {

        // Создаем и добавляем задачу
        Task task = new Task("Original Task", "Description");
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        task.setDuration(Duration.ofHours(1));
        Task addedTask = taskManager.addTask(task);

        // Создаем обновленную версию задачи
        Task updatedTask = new Task("Updated Task", "New Description");
        updatedTask.setId(addedTask.getId()); // Сохраняем тот же ID
        updatedTask.setStartTime(LocalDateTime.of(2025, 1, 1, 9, 0)); // Новое время
        updatedTask.setDuration(Duration.ofMinutes(30));

        // Обновляем задачу
        taskManager.updateTask(updatedTask);

        // Проверяем обновление
        List<Task> prioritized = taskManager.getPrioritizedTasks();

        // Проверяем что задача первая в списке (так как время теперь самое раннее)
        assertEquals(updatedTask, prioritized.get(0));
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 0),
                prioritized.get(0).getStartTime());

        // Дополнительная проверка - старая версия больше не в списке
        assertFalse(prioritized.contains(task));
    }

    @Test
    public void testGetEpicSubtasksWithStream() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getId());
        assertEquals(2, subtasks.size(), "Должны вернуться все подзадачи эпика");
    }

    @Test
    void epicShouldBeNewWhenNoSubtasks() {
        Epic epic = taskManager.addEpic(new Epic("Epic", "Description"));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicShouldBeNewWhenAllSubtasksNew() {
        Epic epic = taskManager.addEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId());
        subtask.setStatus(Status.NEW); // Устанавливаем статус после создания
        taskManager.addSubtask(subtask);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void epicShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = taskManager.addEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask", "Desc", epic.getId());
        subtask.setStatus(Status.DONE); // Устанавливаем статус
        taskManager.addSubtask(subtask);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void epicShouldBeInProgressWhenMixedStatuses() {
        Epic epic = taskManager.addEpic(new Epic("Epic", "Description"));

        Subtask subtask1 = new Subtask("Subtask1", "Desc", epic.getId());
        subtask1.setStatus(Status.NEW);

        Subtask subtask2 = new Subtask("Subtask2", "Desc", epic.getId());
        subtask2.setStatus(Status.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}

