import Objects.Controller;
import Objects.Road;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        while(controller.isRunning()){
            controller.printRoad();
            controller.run();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
