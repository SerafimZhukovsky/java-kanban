import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        handleGetAllTasks(exchange); // GET /tasks
                    } else if (pathParts.length == 3) {
                        handleGetTaskById(exchange, pathParts[2]); // GET /tasks/{id}
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange); // POST /tasks
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteTask(exchange, pathParts[2]); // DELETE /tasks/{id}
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response, 200);
    }

    private void handleGetTaskById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Task task = taskManager.getTaskByID(id);
        if (task == null) {
            sendNotFound(exchange);
            return;
        }
        String response = gson.toJson(task);
        sendText(exchange, response, 200);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == null) {
            // Создание новой задачи
            Task createdTask = taskManager.addTask(task);
            if (createdTask == null) {
                sendHasInteractions(exchange); // 406 если пересекается
                return;
            }
            sendText(exchange, "Задача создана", 201);
        } else {
            // Обновление существующей
            Task updatedTask = taskManager.updateTask(task);
            if (updatedTask == null) {
                sendNotFound(exchange); // 404 если не найдена
                return;
            }
            sendText(exchange, "Задача обновлена", 201);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteTaskByID(id);
        sendText(exchange, "Задача удалена", 200);
    }
}

