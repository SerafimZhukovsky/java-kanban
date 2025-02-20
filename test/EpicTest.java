import org.junit.jupiter.api.Test;
import task.Epic;

import static org.junit.jupiter.api.Assertions.*;


class EpicTest {
    @Test
    public void EpicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("Сделать ремонт", "Уложиться в 2 миллиона");
        epic1.setId(1);
        Epic epic2 = new Epic("Подготовиться к собеседованию", "1 июля в 11:00");
        epic2.setId(1);
        assertEquals(epic1, epic2,
                "Ошибка! Наследники класса task.Task должны быть равны друг другу, если равен их id;");
    }
}

