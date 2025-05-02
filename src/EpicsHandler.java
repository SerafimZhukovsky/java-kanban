import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetAllEpics(exchange); // GET /epics
                    } else if (pathParts.length == 3) {
                        handleGetEpicById(exchange, pathParts[2]); // GET /epics/{id}
                    } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                        handleGetEpicSubtasks(exchange, pathParts[2]); // GET /epics/{id}/subtasks
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(exchange); // POST /epics
                    break;
                case "DELETE":
                    if (pathParts.length == 3) {
                        handleDeleteEpic(exchange, pathParts[2]); // DELETE /epics/{id}
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();
        String response = gson.toJson(epics);
        sendText(exchange, response, 200);
    }

    private void handleGetEpicById(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        Epic epic = taskManager.getEpicByID(id);
        if (epic == null) {
            sendNotFound(exchange);
            return;
        }
        String response = gson.toJson(epic);
        sendText(exchange, response, 200);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
        if (subtasks == null) {
            sendNotFound(exchange);
            return;
        }
        String response = gson.toJson(subtasks);
        sendText(exchange, response, 200);
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == null) {
            // Создание нового эпика
            Epic createdEpic = taskManager.addEpic(epic);
            String response = gson.toJson(Map.of(
                    "message", "Эпик создан",
                    "id", createdEpic.getId()
            ));
            sendText(exchange, "Эпик создан", 201);
        } else {
            // Обновление существующего
            Epic updatedEpic = taskManager.updateEpic(epic);
            if (updatedEpic == null) {
                sendNotFound(exchange); // 404 если не найден
                return;
            }
            sendText(exchange, "Эпик обновлён", 201);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String idStr) throws IOException {
        int id = Integer.parseInt(idStr);
        taskManager.deleteEpicByID(id);
        sendText(exchange, "Эпик удалён", 200);
    }
}
