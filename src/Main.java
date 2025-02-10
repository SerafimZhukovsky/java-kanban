public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task buyMilk = new Task("Купить молоко", "жирность 3,2-6");
        Task buyMilkCreated = taskManager.addTask(buyMilk);
        buyMilk.setStatus(Status.DONE);
        System.out.println(buyMilkCreated + "\n");

        Epic makeLunch = new Epic("Приготовить обед", "Обед из двух блюд");
        taskManager.addEpic(makeLunch);
        Subtask makeLunchSubtask1 = new Subtask("Первое", "Борщ с мясом",
                makeLunch.getId());
        Subtask makeLunchSubtask2 = new Subtask("Второе", "Макароны с сыром",
                makeLunch.getId());
        taskManager.addSubtask(makeLunchSubtask1);
        taskManager.addSubtask(makeLunchSubtask2);





    }
}