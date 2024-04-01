import java.util.concurrent.*;
import java.util.Random;

public class Office {
    private static final int allVisitors = 1000;
    private static int youngVisitors = 0, seniorVisitors = 0, businessVisitors = 0;
    private static int leftYoungVisitors = 0, leftSeniorVisitors = 0, leftBusinessVisitors = 0;

    public static void main(String[] args) throws InterruptedException {
        Semaphore serviceWindowOne = new Semaphore(1);
        Semaphore serviceWindowTwo = new Semaphore(1);
        Semaphore serviceWindowThree = new Semaphore(1);
        Random serviceTime = new Random();

        ExecutorService officeWorkers = Executors.newFixedThreadPool(3);
        for (int i = 0; i < allVisitors; i++) {
            int finalI = i;
            officeWorkers.submit(() -> {
                String visitorCategory = (finalI % 3 == 0) ? "young" : (finalI % 3 == 1) ? "senior" : "business";
                try {
                    switch (visitorCategory) {
                        case "young":
                            youngVisitors++;
                            if (serviceWindowOne.tryAcquire()) {
                                // Получить услугу
                                Thread.sleep(200 + serviceTime.nextInt(800)); // время обслуживания
                                serviceWindowOne.release();
                            } else {
                                leftYoungVisitors++;
                            }
                            break;
                        case "senior":
                            seniorVisitors++;
                            if (serviceWindowTwo.tryAcquire()) {
                                // Получить услугу
                                Thread.sleep(200 + serviceTime.nextInt(800)); // время обслуживания
                                serviceWindowTwo.release();
                            } else {
                                leftSeniorVisitors++;
                            }
                            break;
                        case "business":
                            businessVisitors++;
                            if (serviceWindowThree.tryAcquire()) {
                                // Получить услугу
                                Thread.sleep(200 + serviceTime.nextInt(800)); // время обслуживания
                                serviceWindowThree.release();
                            } else {
                                leftBusinessVisitors++;
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        officeWorkers.shutdown();
        officeWorkers.awaitTermination(1, TimeUnit.DAYS);

        System.out.printf("Общее количество молодых: %d, обслуженные: %d, не обслуженные: %d, %% обслуженных: %.2f%%\n", youngVisitors, youngVisitors - leftYoungVisitors, leftYoungVisitors, (double) (youngVisitors - leftYoungVisitors) / youngVisitors * 100);
        System.out.printf("Общее количество пожилых: %d, обслуженные: %d, не обслуженные: %d, %% обслуженных: %.2f%%\n", seniorVisitors, seniorVisitors - leftSeniorVisitors, leftSeniorVisitors, (double) (seniorVisitors - leftSeniorVisitors) / seniorVisitors * 100);
        System.out.printf("Общее количество бизнесменов: %d, обслуженные: %d, не обслуженные: %d, %% обслуженных: %.2f%%\n", businessVisitors, businessVisitors - leftBusinessVisitors, leftBusinessVisitors, (double) (businessVisitors - leftBusinessVisitors) / businessVisitors * 100);
    }
}
