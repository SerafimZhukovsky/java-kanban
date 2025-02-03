public class Subtask extends Task {

    private Integer epicID;
    public Subtask(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
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
