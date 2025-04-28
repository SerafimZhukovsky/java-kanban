import enums.Status;
import enums.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask result = super.addSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task task) {
        Task result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskByID(Integer id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(Integer id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        super.deleteSubtaskByID(id);
        save();
    }

    private void save() {
        try {
            String header = "id,type,name,status,description,epic, duration,startTime\n";
            Files.writeString(file.toPath(), header);

            for (Task task : getTasks()) {
                Files.writeString(file.toPath(), taskToString(task) + "\n",
                        StandardOpenOption.APPEND);
            }

            for (Epic epic : getEpics()) {
                Files.writeString(file.toPath(), taskToString(epic) + "\n",
                        StandardOpenOption.APPEND);
            }

            for (Subtask subtask : getSubtasks()) {
                Files.writeString(file.toPath(), taskToString(subtask) + "\n",
                        StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    private String taskToString(Task task) {
        String durationStr = "";
        if (task.getDuration() != null) {
            durationStr = String.valueOf(task.getDuration().toMinutes());
        }

        String startTimeStr = "";
        if (task.getStartTime() != null) {
            startTimeStr = task.getStartTime().toString();
        }

        if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.EPIC,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                    subtask.getId(),
                    TaskType.SUBTASK,
                    subtask.getName(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicID(),
                    durationStr,
                    startTimeStr);
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    durationStr,
                    startTimeStr);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            // Пропускаем заголовок
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.getEpicsMap().put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.getSubtasksMap().put(task.getId(), (Subtask) task);
                    Epic epic = manager.getEpicsMap().get(((Subtask) task).getEpicID());
                    if (epic != null) {
                        epic.addSubtask((Subtask) task);
                    }
                } else {
                    manager.getTasksMap().put(task.getId(), task);
                    manager.addToPrioritized(task);
                }

                // Обновляем счетчик id
                if (manager.getNextId() <= task.getId()) {
                    manager.setNextId(task.getId() + 1);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки файла", e);
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Duration duration = null;
        if (parts.length > 5 && !parts[5].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        }

        LocalDateTime startTime = null;
        if (parts.length > 6 && !parts[6].isEmpty()) {
            startTime = LocalDateTime.parse(parts[6]);
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status);
                subtask.setId(id);
                subtask.setEpicID(epicId);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }
}
