import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import enums.Status;
import task.Task;
import task.Subtask;
import task.Epic;

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
}
