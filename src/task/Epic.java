package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private LocalDateTime endTime;
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void updateTime() {
        if (subtaskList.isEmpty()) {
            this.startTime = null;
            this.duration = null;
            this.endTime = null;
            return;
        }

        this.startTime = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(time -> time != null)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtaskList.stream()
                .map(Subtask::getDuration)
                .filter(dur -> dur != null)
                .reduce(Duration.ZERO, Duration::plus);

        this.endTime = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(time -> time != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
        updateTime();
    }

    public void clearSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtaskList.remove(subtask);
        updateTime();
    }

    public void clearSubtasks() {
        subtaskList.clear();
        updateTime();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtaskList);
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}
