import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Купить хлеб", "В Дикси у дома");
        task1.setId(10);
        Task task2 = new Task("Купить молоко", "В Пятерочке");
        task2.setId(10);
        assertEquals(task1, task2,
                "Ошибка! Экземпляры класса task.Task должны быть равны друг другу, если равен их id;");
    }
}