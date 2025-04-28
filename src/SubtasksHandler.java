import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllSubtasks(exchange); // GET /subtasks
                    } else if (pathParts.length == 3) {
                        handleGetSubtaskById(exchange, pathParts[2]); // GET /subtasks/{id}
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(exchange); // POST /subtasks
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteSubtask(exchange, pathParts[2]); // DELETE /subtasks/{id}
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtasks();
        String response = gson.toJson(subtasks);
        sendText(exchange, response, 200);
    }

    private void handleGetSubtaskById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Subtask subtask = taskManager.getSubtaskByID(id);
        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }
        String response = gson.toJson(subtask);
        sendText(exchange, response, 200);
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == null) {
            // Создание новой подзадачи
            Subtask createdSubtask = taskManager.addSubtask(subtask);
            if (createdSubtask == null) {
                sendHasInteractions(exchange); // 406 если пересекается
                return;
            }
            sendText(exchange, "Подзадача создана", 201);
        } else {
            // Обновление существующей
            Subtask updatedSubtask = taskManager.updateSubtask(subtask);
            if (updatedSubtask == null) {
                sendNotFound(exchange); // 404 если не найдена
                return;
            }
            sendText(exchange, "Подзадача обновлена", 201);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteSubtaskByID(id);
        sendText(exchange, "Подзадача удалена", 200);
    }
}