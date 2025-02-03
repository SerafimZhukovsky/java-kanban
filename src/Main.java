public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task buyMilk = new Task("Купить молоко", "жирность 3,2-6");
        Task buyMilkCreated = taskManager.addTask(buyMilk);
        buyMilk.setStatus(Status.NEW);
        System.out.println(buyMilkCreated);
        buyMilk.setStatus(Status.IN_PROGRESS);
        System.out.println(buyMilkCreated);
        buyMilk.setStatus(Status.DONE);
        System.out.println(buyMilkCreated);


        Epic makeLunch = new Epic("Приготовить обед", "Обед из двух блюд");
        taskManager.addEpic(makeLunch);
        Subtask makeLunchSubtask1 = new Subtask("Первое", "Борщ с мясом",
                makeLunch.getId());
        Subtask makeLunchSubtask2 = new Subtask("Второе", "Макароны с сыром",
                makeLunch.getId());
        taskManager.addSubtask(makeLunchSubtask1);
        taskManager.addSubtask(makeLunchSubtask2);
        System.out.println(makeLunchSubtask1);
        System.out.println(makeLunchSubtask2);
        makeLunchSubtask1.setStatus(Status.DONE);
        makeLunchSubtask2.setStatus(Status.DONE);
        System.out.println(makeLunchSubtask1);
        System.out.println(makeLunchSubtask2);
        taskManager.updateSubtask(makeLunchSubtask2);
        System.out.println(makeLunch);

    }
}