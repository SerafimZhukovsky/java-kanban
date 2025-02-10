import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private Integer id = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private Integer getid() {
        return id++;
    }

    public Task addTask(Task task) {
        task.setId(getid());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(getid());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        if (subtask.getEpicID() != null || epics.containsKey(subtask.getEpicID())) {
            subtask.setId(getid());
            Epic epic = epics.get(subtask.getEpicID());
            epic.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
            return subtask;
        } else {
            System.out.println("Эпика не существует, субтаск не добавлен");
            subtask = null;
            return subtask;
        }
    }

    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (taskID == null || !tasks.containsKey(taskID)) {
            return null;
        }
        tasks.replace(taskID, task);
        return task;
    }

    public void updateEpic(Epic epic) {
        // проверяем, что данный эпик не null и он у нас был
        if (epic == null){
            return;
        }
        Integer epicID = epic.getId();
        if (epicID == null || !epics.containsKey(epicID)) {
            return;
        }
        // Обновляем только name и description
        Epic existingEpic = epics.get(epicID);
        // Копируем обновлённые поля
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        Integer subtaskId = subtask.getId();
        if ( subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return; //
        }
        // Проверка совпадения epicId
        Integer currentEpicId = subtasks.get(subtaskId).getEpicID();
        if (!currentEpicId.equals(subtask.getEpicID())) {
            System.out.println("Неверный EpicID подзадачи");
            return;
        }

        Epic epic = epics.get(currentEpicId); //
        if (epic != null) {
            epic.clearSubtask(subtask);
        }
        // Обновление подзадачи в хранилище subtasks
        subtasks.replace(subtaskId, subtask);

        // Обновление подзадачи в эпике
            epic.addSubtask(subtask);

        updateEpicStatus(epic);
    }


    public Task getTaskByID(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(Integer id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(Integer id) {
        Epic epic = epics.get(id); // Получаем эпик по ID
        if (epic == null) {
            return new ArrayList<>(); // Возвращаем пустой список, если эпик не найден
        }
        return new ArrayList<>(epic.getSubtaskList()); // Защитная копия списка подзадач
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }

    }

    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(Integer id) {
        if (!epics.containsKey(id)){
            return;
        }
        ArrayList<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        epics.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
    }

    public void deleteSubtaskByID(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicID = subtask.getEpicID();
            subtasks.remove(id);
            // обновляем список подзадач и статус эпика

            Epic epic = epics.get(epicID);
            ArrayList<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            epic.clearSubtask(subtask);
        }
    }

    // вспомогательный private метод для контроля статуса эпика при удалении или изменении подзадач
    private void updateEpicStatus(Epic epic) {
        if (epic == null) {  // Добавили проверку на null
            return;
        }
        ArrayList<Subtask> list = epic.getSubtaskList();
        if (list.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int allIsDoneCount = 0;
        int allIsInNewCount = 0;
        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allIsDoneCount++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allIsInNewCount++;
            }
        }
        if (allIsInNewCount == list.size()) {
            epic.setStatus(Status.NEW);
        } else if (allIsDoneCount == list.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}



