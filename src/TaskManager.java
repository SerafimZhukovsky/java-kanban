import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task getTaskByID(Integer id);

    Epic getEpicByID(Integer id);

    Subtask getSubtaskByID(Integer id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(Integer id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTaskByID(Integer id);

    void deleteEpicByID(Integer id);

    void deleteSubtaskByID(Integer id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isOverlapping(Task task1, Task task2);
}
