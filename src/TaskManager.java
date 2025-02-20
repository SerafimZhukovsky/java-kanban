import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Integer getid();

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task getTaskByID(Integer id);

    Epic getEpicByID(Integer id);

    Subtask getSubtaskByID(Integer id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getEpicSubtasks(Integer id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(Integer id);

    void deleteEpicByID(Integer id);

    void deleteSubtaskByID(Integer id);

    // вспомогательный private метод для контроля статуса эпика при удалении или изменении подзадач
    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
