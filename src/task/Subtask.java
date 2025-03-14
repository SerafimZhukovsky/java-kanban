package task;

import enums.Status;
import task.Task;

public class Subtask extends Task {

    private Integer epicID;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicID=" + epicID +
                ", status=" + getStatus() +
                '}';
    }

    public Integer getEpicID() {
        return epicID;

    }
}
