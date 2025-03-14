import enums.Status;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    private Integer getid() {
        return id++;
    }

    @Override
    public Task addTask(Task task) {
        task.setId(getid());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getid());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
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

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (taskID == null || !tasks.containsKey(taskID)) {
            return null;
        }
        tasks.replace(taskID, task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        // проверяем, что данный эпик не null и он у нас был
        if (epic == null){
            return epic;
        }
        Integer epicID = epic.getId();
        if (epicID == null || !epics.containsKey(epicID)) {
            return epic;
        }
        // Обновляем только name и description
        Epic existingEpic = epics.get(epicID);
        // Копируем обновлённые поля
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return subtask;
        }

        Integer subtaskId = subtask.getId();
        if ( subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return subtask;
        }
        // Проверка совпадения epicId
        Integer currentEpicId = subtasks.get(subtaskId).getEpicID();
        if (!currentEpicId.equals(subtask.getEpicID())) {
            System.out.println("Неверный EpicID подзадачи");
            return subtask;
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
        return subtask;
    }


    @Override
    public Task getTaskByID(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Integer id) {
        Epic epic = epics.get(id); // Получаем эпик по ID
        if (epic == null) {
            return new ArrayList<>(); // Возвращаем пустой список, если эпик не найден
        }
        return new ArrayList<>(epic.getSubtaskList()); // Защитная копия списка подзадач
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }

    }

    @Override
    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void deleteSubtaskByID(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicID = subtask.getEpicID();
            subtasks.remove(id);
            // обновляем список подзадач и статус эпика

            Epic epic = epics.get(epicID);
            epic.clearSubtask(subtask);
            updateEpicStatus(epic);
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}



