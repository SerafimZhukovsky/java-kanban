import enums.Status;
import org.junit.jupiter.api.Test;
import task.Subtask;

import static org.junit.jupiter.api.Assertions.*;
class SubtaskTest {

    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Купить хлеб", "В Дикси у дома", 5);
        subtask1.setId(10);
        Subtask subtask2 = new Subtask("Купить молоко", "В Пятерочке", 5);
        subtask2.setId(10);
        assertEquals(subtask1, subtask2,
                "Ошибка! Наследники класса task.Task должны быть равны друг другу, если равен их id;");
    }
}

