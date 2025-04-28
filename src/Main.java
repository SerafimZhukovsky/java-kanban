import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager manager = Managers.getDefault(); // Получаем менеджер задач
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}