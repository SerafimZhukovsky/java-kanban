import enums.Status;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private Integer id = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

    private Integer getid() {
        return id++;
    }

    @Override
    public Task addTask(Task task) {
        if (task == null || hasOverlap(task)) {
            return null;
        }
        Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        copyTask.setId(getid());
        copyTask.setDuration(task.getDuration());
        copyTask.setStartTime(task.getStartTime());
        tasks.put(copyTask.getId(), copyTask);
        updatePrioritizedTasks(copyTask);

        return copyTask;
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
            if (hasOverlap(subtask)) {
                subtask = null;
                return subtask;
            }
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
        if (taskID == null || !tasks.containsKey(taskID) || hasOverlap(task)) {
            return null;
        }

        Task oldTask = tasks.get(task.getId());
        removeFromPrioritizedTasks(oldTask);

        tasks.replace(taskID, task);
        updatePrioritizedTasks(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        // проверяем, что данный эпик не null и он у нас был
        if (epic == null) {
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
        Integer subtaskId = subtask.getId();
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return subtask;
        }

        // Проверка совпадения epicId
        Integer currentEpicId = subtasks.get(subtaskId).getEpicID();
        if (!currentEpicId.equals(subtask.getEpicID())) {
            System.out.println("Неверный EpicID подзадачи");
            return subtask;
        }

        if (hasOverlap(subtask)) {
            subtask = null;
            return subtask;
        }

        Epic epic = epics.get(currentEpicId); //
        if (epic != null) {
            epic.clearSubtask(subtask);
        }
        // Обновление подзадачи в хранилище subtasks
        subtasks.replace(subtaskId, subtask);
        updatePrioritizedTasks(subtask);

        // Обновление подзадачи в эпике
        epic.addSubtask(subtask);

        updateEpicStatus(epic);
        return subtask;
    }


    @Override
    public Task getTaskByID(Integer id) {
        if (id == null || !tasks.containsKey(id)) {
            return null; // Если задача не найдена, возвращаем null
        }
        Task task = tasks.get(id);
        Task copy = new Task(task.getName(), task.getDescription(), task.getStatus());
        copy.setId(task.getId());
        historyManager.add(copy);
        return copy;
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
    public List<Subtask> getEpicSubtasks(Integer id) {
        return subtasks.values().stream().filter(subtask -> subtask.getEpicID().equals(id)).collect(Collectors.toList());
    }

    @Override
    public void deleteTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (Subtask subtask : epic.getSubtaskList()) {
                historyManager.remove(subtask.getId());
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }

    }

    @Override
    public void deleteTaskByID(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            Task task = tasks.get(id);
            removeFromPrioritizedTasks(task);
            tasks.remove(id);
        }
    }

    @Override
    public void deleteEpicByID(Integer id) {
        if (!epics.containsKey(id)) {
            return;
        }
        ArrayList<Subtask> epicSubtasks = epics.get(id).getSubtaskList();
        epics.remove(id);
        historyManager.remove(id);
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicID = subtask.getEpicID();
            subtasks.remove(id);
            historyManager.remove(id);
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
        List<Subtask> listSubtasks = epic.getSubtaskList();
        if (listSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        long doneCount = listSubtasks.stream().filter(subtask -> subtask.getStatus() == Status.DONE).count();

        long newCount = listSubtasks.stream().filter(subtask -> subtask.getStatus() == Status.NEW).count();
        if (newCount == listSubtasks.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == listSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected Map<Integer, Task> getTasksMap() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicsMap() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }

    protected int getNextId() {
        return id;
    }

    protected void setNextId(int newId) {
        this.id = newId;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    public boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null || task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasOverlap(Task task) {
        return prioritizedTasks.stream().anyMatch(t -> isOverlapping(t, task));
    }

    protected void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }
}





